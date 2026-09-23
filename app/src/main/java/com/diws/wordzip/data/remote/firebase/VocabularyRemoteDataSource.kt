package com.diws.wordzip.data.remote.firebase

interface VocabularyRemoteDataSource {
    suspend fun getChangedWordsSince(lastSyncedAt: Long): List<FirebaseWordDto>
    suspend fun getAllWords(): List<FirebaseWordDto>
}
