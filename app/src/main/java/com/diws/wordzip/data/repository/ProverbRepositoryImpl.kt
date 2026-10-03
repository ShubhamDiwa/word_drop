package com.diws.wordzip.data.repository

import android.util.Log
import com.diws.wordzip.data.local.DailyProverbEntity
import com.diws.wordzip.data.local.InitialProverbSeedData
import com.diws.wordzip.data.local.ProverbDao
import com.diws.wordzip.data.local.ProverbEntity
import com.diws.wordzip.domain.model.Proverb
import com.diws.wordzip.domain.repository.ProverbRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProverbRepositoryImpl @Inject constructor(
    private val proverbDao: ProverbDao,
    private val firestore: FirebaseFirestore?
) : ProverbRepository {

    companion object {
        private const val TAG = "ProverbRepository"
        val CANDIDATE_PROVERB_COLLECTIONS = listOf(
            "proverbs",
            "Proverbs",
            "proverb_list",
            "proverbs_list",
            "kahawat",
            "kahawate"
        )
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            populateSeedDataIfNeeded()
            syncProverbsWithRemote()
        }
    }

    private suspend fun populateSeedDataIfNeeded() {
        val count = proverbDao.getCount()
        if (count == 0) {
            proverbDao.insertProverbs(InitialProverbSeedData.getInitialProverbs())
        }
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun getAllProverbs(): Flow<List<Proverb>> {
        return proverbDao.getAllProverbs().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getProverbFlow(id: String): Flow<Proverb?> {
        return proverbDao.getProverbByIdFlow(id).map { it?.toDomainModel() }
    }

    override suspend fun getProverbById(id: String): Proverb? {
        return proverbDao.getProverbById(id)?.toDomainModel()
    }

    override fun getFavoriteProverbs(): Flow<List<Proverb>> {
        return proverbDao.getFavoriteProverbs().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getProverbsByCategory(category: String): Flow<List<Proverb>> {
        return proverbDao.getProverbsByCategory(category).map { list -> list.map { it.toDomainModel() } }
    }

    override fun searchProverbs(query: String): Flow<List<Proverb>> {
        return proverbDao.searchProverbs(query).map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun getDailyProverb(): Proverb? = withContext(Dispatchers.IO) {
        populateSeedDataIfNeeded()
        val today = getTodayDateString()
        val existingDaily = proverbDao.getDailyProverb(today)
        if (existingDaily != null) {
            val proverb = proverbDao.getProverbById(existingDaily.proverbId)
            if (proverb != null) {
                return@withContext proverb.toDomainModel()
            }
        }

        // Pick a random proverb
        val randomProverb = proverbDao.getRandomProverb() ?: return@withContext null
        proverbDao.insertDailyProverb(DailyProverbEntity(date = today, proverbId = randomProverb.id))
        proverbDao.updateProverbShownStats(randomProverb.id, System.currentTimeMillis())
        return@withContext randomProverb.toDomainModel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDailyProverbFlow(): Flow<Proverb?> {
        val today = getTodayDateString()
        return proverbDao.getDailyProverbFlow(today).flatMapLatest { dailyEntity ->
            if (dailyEntity != null) {
                proverbDao.getProverbByIdFlow(dailyEntity.proverbId).map { it?.toDomainModel() }
            } else {
                flowOf(null)
            }
        }
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        proverbDao.updateFavoriteStatus(id, isFavorite)
    }

    override suspend fun syncProverbsWithRemote(): Result<Int> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.success(0)
        try {
            val allRemoteEntities = mutableListOf<ProverbEntity>()
            val seenIds = mutableSetOf<String>()

            for (collName in CANDIDATE_PROVERB_COLLECTIONS) {
                try {
                    val snapshot = db.collection(collName).get().await()
                    if (!snapshot.isEmpty) {
                        for (doc in snapshot.documents) {
                            val entities = parseProverbDoc(doc)
                            for (entity in entities) {
                                if (seenIds.add(entity.id)) {
                                    val existing = proverbDao.getProverbById(entity.id)
                                    val finalEntity = entity.copy(
                                        isFavorite = existing?.isFavorite ?: entity.isFavorite,
                                        timesShown = existing?.timesShown ?: entity.timesShown,
                                        lastShownAt = existing?.lastShownAt ?: entity.lastShownAt
                                    )
                                    allRemoteEntities.add(finalEntity)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Fetch from collection '$collName' failed or skipped: ${e.message}")
                }
            }

            if (allRemoteEntities.isNotEmpty()) {
                proverbDao.insertProverbs(allRemoteEntities)
                Log.d(TAG, "Successfully synced ${allRemoteEntities.size} proverbs from Firestore")
            }
            Result.success(allRemoteEntities.size)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync proverbs with Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    private fun parseProverbDoc(doc: com.google.firebase.firestore.DocumentSnapshot): List<ProverbEntity> {
        val data = doc.data ?: return emptyList()

        // Check if document contains an array of proverbs
        val candidateArrayKeys = listOf("proverbs", "Proverbs", "items", "Items", "list", "List", "data", "Data", "proverbList")
        for (key in candidateArrayKeys) {
            val list = data[key] as? List<*>
            if (!list.isNullOrEmpty()) {
                val parsedList = list.mapNotNull { item ->
                    if (item is Map<*, *>) parseMapToProverb(item, doc.id) else null
                }
                if (parsedList.isNotEmpty()) return parsedList
            }
        }

        // Single document
        val single = parseMapToProverb(data, doc.id)
        return if (single != null) listOf(single) else emptyList()
    }

    private fun parseMapToProverb(data: Map<*, *>, fallbackId: String): ProverbEntity? {
        val englishText = (data["englishText"] as? String)?.trim()
            ?: (data["english"] as? String)?.trim()
            ?: (data["proverb"] as? String)?.trim()
            ?: (data["text"] as? String)?.trim()
            ?: (data["title"] as? String)?.trim()
            ?: return null

        if (englishText.isBlank()) return null

        val id = (data["id"] as? String)?.takeIf { it.isNotBlank() }
            ?: (data["proverbId"] as? String)?.takeIf { it.isNotBlank() }
            ?: fallbackId.ifBlank { "proverb_${englishText.take(15).lowercase().replace(Regex("[^a-z0-9]"), "_")}" }

        val hindiText = (data["hindiText"] as? String)?.trim()
            ?: (data["hindi"] as? String)?.trim()
            ?: (data["meaning_hi"] as? String)?.trim()
            ?: (data["hindiMeaning"] as? String)?.trim()
            ?: ""

        val hindiEquivalent = (data["hindiEquivalent"] as? String)?.trim()
            ?: (data["kahawat"] as? String)?.trim()
            ?: (data["equivalent"] as? String)?.trim()

        val meaningEnglish = (data["meaningEnglish"] as? String)?.trim()
            ?: (data["definition"] as? String)?.trim()
            ?: (data["meaning"] as? String)?.trim()
            ?: englishText

        val meaningHindi = (data["meaningHindi"] as? String)?.trim()
            ?: (data["hindi_meaning"] as? String)?.trim()
            ?: hindiText

        val example = (data["example"] as? String)?.trim()
            ?: (data["usage"] as? String)?.trim()
            ?: (data["sentence"] as? String)?.trim()

        val category = (data["category"] as? String)?.trim()
            ?: (data["tag"] as? String)?.trim()
            ?: "Wisdom"

        return ProverbEntity(
            id = id,
            englishText = englishText,
            hindiText = hindiText,
            hindiEquivalent = hindiEquivalent,
            meaningEnglish = meaningEnglish,
            meaningHindi = meaningHindi,
            example = example,
            category = category,
            isFavorite = false,
            timesShown = 0,
            lastShownAt = null
        )
    }
}
