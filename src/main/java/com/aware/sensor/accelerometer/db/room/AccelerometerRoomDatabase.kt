package com.aware.sensor.accelerometer.db.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Room accelerometer database class.
 * Note that creating new instances of Room db is costly.
 *
 * @author  sercant
 * @date 23/02/2018
 */
@Database(entities = arrayOf(AccelerometerData::class), version = 1)
abstract class AccelerometerRoomDatabase : RoomDatabase() {

    abstract fun AccelerometerDataDao(): AccelerometerDataDao

    companion object {
        private var INSTANCE: AccelerometerRoomDatabase? = null

        fun getInstance(context: Context): AccelerometerRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(AccelerometerRoomDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AccelerometerRoomDatabase::class.java, "accelerometer.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}