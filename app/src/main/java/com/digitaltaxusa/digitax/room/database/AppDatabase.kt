package com.digitaltaxusa.digitax.room.database

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.digitaltaxusa.digitax.room.dao.DrivingDao
import com.digitaltaxusa.digitax.room.dao.TaxDeductionDao
import com.digitaltaxusa.digitax.room.dao.UserSessionDao
import com.digitaltaxusa.digitax.room.entity.DrivingEntity
import com.digitaltaxusa.digitax.room.entity.TaxDeductionEntity
import com.digitaltaxusa.digitax.room.entity.UserSessionEntity

@Database(entities = [
    DrivingEntity::class,
    TaxDeductionEntity::class,
    UserSessionEntity::class],
    version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun drivingDao(): DrivingDao
    abstract fun taxDeductionDao(): TaxDeductionDao
    abstract fun userSessionDao(): UserSessionDao

    companion object {
        @VisibleForTesting
        const val DATABASE_NAME = "room.db"

        @Volatile
        private var instance: AppDatabase? = null

        /**
         * This method should be called in the Application so the context parameter can
         * be set to application context since this object will live during the entire
         * application life cycle.
         *
         * Utilizes singleton pattern by restricting the instantiation of this class
         * and ensuring that only one instance of the class exists in the java virtual machine.
         *
         * @param context Interface to global information about an application environment.
         * @return Instance of class.
         */
        @Synchronized
        fun getDatabase(context: Context): AppDatabase? {
            if (instance == null) {
                // all synchronized blocks synchronized on the same object can only have
                // one thread executing inside them at a time. All other threads attempting
                // to enter the synchronized block are blocked until the thread inside the
                // synchronized block exits the block
                instance = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}