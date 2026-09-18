package com.diws.worddrop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [WordEntity::class, DailyWordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        const val DATABASE_NAME = "worddrop_db"

        @Volatile
        private var INSTANCE: WordDatabase? = null

        fun getInstance(context: android.content.Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `daily_words` (`date` TEXT NOT NULL, `wordId` TEXT NOT NULL, `assignedAt` INTEGER NOT NULL, PRIMARY KEY(`date`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `words_new` (`id` TEXT NOT NULL, `word` TEXT NOT NULL, `pronunciation` TEXT, `partOfSpeech` TEXT, `definition` TEXT, `simpleMeaning` TEXT, `example` TEXT, `synonyms` TEXT, `difficulty` TEXT, `category` TEXT, `audioUrl` TEXT, `isLearned` INTEGER NOT NULL, `timesShown` INTEGER NOT NULL, `lastShownAt` INTEGER, PRIMARY KEY(`id`))")
                database.execSQL("INSERT INTO `words_new` (`id`,`word`,`pronunciation`,`partOfSpeech`,`definition`,`simpleMeaning`,`example`,`synonyms`,`difficulty`,`category`,`audioUrl`,`isLearned`,`timesShown`,`lastShownAt`) SELECT `id`,`word`,`pronunciation`,`partOfSpeech`,`definition`,`simpleMeaning`,`example`,`synonyms`,`difficulty`,`category`,`audioUrl`,`isLearned`,`timesShown`,`lastShownAt` FROM `words`")
                database.execSQL("DROP TABLE `words`")
                database.execSQL("ALTER TABLE `words_new` RENAME TO `words`")
            }
        }
    }
}
