package com.diws.worddrop.di

import android.content.Context
import androidx.room.Room
import com.diws.worddrop.data.local.WordDao
import com.diws.worddrop.data.local.WordDatabase
import com.diws.worddrop.data.preferences.UserPreferencesRepository
import com.diws.worddrop.data.remote.DictionaryApi
import com.diws.worddrop.data.remote.firebase.FirebaseVocabularyDataSource
import com.diws.worddrop.data.remote.firebase.VocabularyRemoteDataSource
import com.diws.worddrop.data.repository.WordRepositoryImpl
import com.diws.worddrop.domain.repository.WordRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWordDatabase(@ApplicationContext context: Context): WordDatabase {
        return Room.databaseBuilder(
            context,
            WordDatabase::class.java,
            WordDatabase.DATABASE_NAME
        )
            .addMigrations(WordDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: WordDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideDictionaryApi(okHttpClient: OkHttpClient): DictionaryApi {
        return Retrofit.Builder()
            .baseUrl(DictionaryApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(@ApplicationContext context: Context): FirebaseFirestore? {
        return try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else null
        } catch (_: Exception) {
            null
        }
    }

    @Provides
    @Singleton
    fun provideVocabularyRemoteDataSource(
        @ApplicationContext context: Context,
        firestore: FirebaseFirestore?
    ): VocabularyRemoteDataSource {
        return FirebaseVocabularyDataSource(context, firestore)
    }

    @Provides
    @Singleton
    fun provideWordRepository(
        wordDao: WordDao,
        dictionaryApi: DictionaryApi,
        vocabularyRemoteDataSource: VocabularyRemoteDataSource,
        userPreferencesRepository: UserPreferencesRepository
    ): WordRepository {
        return WordRepositoryImpl(
            wordDao = wordDao,
            dictionaryApi = dictionaryApi,
            vocabularyRemoteDataSource = vocabularyRemoteDataSource,
            userPreferencesRepository = userPreferencesRepository
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }
}
