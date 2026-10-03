package com.diws.wordzip.data.remote.translation

import android.util.Log
import com.diws.wordzip.domain.model.WordTranslation
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

interface BhashiniApi {
    @POST("v1/pipeline/inference")
    @Headers("Content-Type: application/json")
    suspend fun computeInference(
        @Header("Authorization") apiKey: String?,
        @Body request: BhashiniInferenceRequest
    ): BhashiniInferenceResponse
}

data class BhashiniInferenceRequest(
    @SerializedName("pipelineTasks")
    val pipelineTasks: List<BhashiniPipelineTask>,
    @SerializedName("inputData")
    val inputData: BhashiniInputData
)

data class BhashiniPipelineTask(
    @SerializedName("taskType")
    val taskType: String = "translation",
    @SerializedName("config")
    val config: BhashiniTaskConfig
)

data class BhashiniTaskConfig(
    @SerializedName("language")
    val language: BhashiniLanguageConfig
)

data class BhashiniLanguageConfig(
    @SerializedName("sourceLanguage")
    val sourceLanguage: String,
    @SerializedName("targetLanguage")
    val targetLanguage: String
)

data class BhashiniInputData(
    @SerializedName("input")
    val input: List<BhashiniInputSentence>
)

data class BhashiniInputSentence(
    @SerializedName("source")
    val source: String
)

data class BhashiniInferenceResponse(
    @SerializedName("pipelineResponse")
    val pipelineResponse: List<BhashiniPipelineResponseItem>?
)

data class BhashiniPipelineResponseItem(
    @SerializedName("taskType")
    val taskType: String?,
    @SerializedName("output")
    val output: List<BhashiniOutputSentence>?
)

data class BhashiniOutputSentence(
    @SerializedName("source")
    val source: String?,
    @SerializedName("target")
    val target: String?
)

@Singleton
class BhashiniTranslationService @Inject constructor(
    private val okHttpClient: OkHttpClient
) : TranslationDataSource {

    companion object {
        private const val TAG = "TranslationService"
    }

    private val bhashiniApi: BhashiniApi? by lazy {
        try {
            Retrofit.Builder()
                .baseUrl("https://dhruva-api.bhashini.gov.in/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BhashiniApi::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Bhashini API client", e)
            null
        }
    }

    override suspend fun translateText(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext Result.success("")
        val cleanTarget = targetLanguage.lowercase().trim()
        val cleanSource = sourceLanguage.lowercase().trim()

        if (cleanTarget == cleanSource || cleanTarget == "en") {
            return@withContext Result.success(text)
        }

        // 1. Try Google Translate public API (Instant, high accuracy, supports all Indian languages)
        try {
            val encoded = URLEncoder.encode(text, "UTF-8")
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$cleanSource&tl=$cleanTarget&dt=t&q=$encoded"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val jsonArray = JsonParser.parseString(body).asJsonArray
                    val sentences = jsonArray.get(0).asJsonArray
                    val sb = StringBuilder()
                    for (i in 0 until sentences.size()) {
                        val sentenceArray = sentences.get(i).asJsonArray
                        if (sentenceArray.size() > 0 && !sentenceArray.get(0).isJsonNull) {
                            sb.append(sentenceArray.get(0).asString)
                        }
                    }
                    val translated = sb.toString().trim()
                    if (translated.isNotBlank()) {
                        Log.d(TAG, "Google Translate succeeded for '$cleanTarget': '$translated'")
                        return@withContext Result.success(translated)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Google Translate attempt failed: ${e.message}")
        }

        // 2. Fallback to MyMemory translation API
        try {
            val encoded = URLEncoder.encode(text, "UTF-8")
            val mmUrl = "https://api.mymemory.translated.net/get?q=$encoded&langpair=$cleanSource|$cleanTarget"
            val request = Request.Builder().url(mmUrl).build()
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val json = JsonParser.parseString(body).asJsonObject
                    val resData = json.getAsJsonObject("responseData")
                    val translated = resData?.get("translatedText")?.asString?.trim()
                    if (!translated.isNullOrBlank() && !translated.startsWith("MYMEMORY WARNING", ignoreCase = true)) {
                        Log.d(TAG, "MyMemory succeeded for '$cleanTarget': '$translated'")
                        return@withContext Result.success(translated)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "MyMemory attempt failed: ${e.message}")
        }

        // 3. Fallback to Bhashini API
        val api = bhashiniApi
        if (api != null) {
            try {
                val request = BhashiniInferenceRequest(
                    pipelineTasks = listOf(
                        BhashiniPipelineTask(
                            config = BhashiniTaskConfig(
                                language = BhashiniLanguageConfig(
                                    sourceLanguage = cleanSource,
                                    targetLanguage = cleanTarget
                                )
                            )
                        )
                    ),
                    inputData = BhashiniInputData(
                        input = listOf(BhashiniInputSentence(source = text))
                    )
                )

                val response = api.computeInference(apiKey = null, request = request)
                val translated = response.pipelineResponse
                    ?.firstOrNull { it.taskType == "translation" }
                    ?.output
                    ?.firstOrNull()
                    ?.target

                if (!translated.isNullOrBlank()) {
                    return@withContext Result.success(translated)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Bhashini translation failed: ${e.message}")
            }
        }

        Result.failure(Exception("Unable to translate text to $cleanTarget"))
    }

    override suspend fun translateWord(
        wordId: String,
        englishMeaning: String,
        targetLanguages: List<String>
    ): List<WordTranslation> {
        val results = mutableListOf<WordTranslation>()
        for (lang in targetLanguages) {
            val res = translateText(englishMeaning, sourceLanguage = "en", targetLanguage = lang)
            res.getOrNull()?.let { translatedText ->
                results.add(
                    WordTranslation(
                        languageCode = lang,
                        meaning = translatedText
                    )
                )
            }
        }
        return results
    }
}

