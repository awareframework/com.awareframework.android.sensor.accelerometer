package com.awareframework.android.sensor.accelerometer.db.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.commonsware.cwac.saferoom.SafeHelperFactory

/**
 * Room accelerometer database class.
 * Note that creating new instances of Room db is costly.
 *
 * @author  sercant
 * @date 23/02/2018
 */
@Database(entities = arrayOf(EventRoomEntity::class, DeviceRoomEntity::class), version = 6)
abstract class AccelerometerRoomDatabase : RoomDatabase() {

    abstract fun AccelerometerEventDao(): AccelerometerEventDao
    abstract fun AccelerometerDeviceDao(): AccelerometerDeviceDao

    companion object {
        /**
         * This creating instance is expensive and should be closed by calling `close()` after db is no
         * longer needed.
         */
        fun getInstance(context: Context, encryptionKey: String?, dbName: String): AccelerometerRoomDatabase {
            val builder = Room.databaseBuilder(context.applicationContext,
                    AccelerometerRoomDatabase::class.java, dbName)
            if (encryptionKey != null) {
                builder.openHelperFactory(SafeHelperFactory(encryptionKey.toCharArray()))
            }

            val instance: AccelerometerRoomDatabase = builder
                    // TODO (sercant): handle migrations!
                    .fallbackToDestructiveMigration()
                    .build()

            return instance
        }
    }
}