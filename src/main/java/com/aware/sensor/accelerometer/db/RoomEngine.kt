package com.aware.sensor.accelerometer.db

import android.content.Context
import com.aware.sensor.accelerometer.db.room.AccelerometerData
import com.aware.sensor.accelerometer.db.room.AccelerometerRoomDatabase
import com.aware.sensor.accelerometer.model.AccelerometerDevice
import com.aware.sensor.accelerometer.model.AccelerometerEvent
import kotlin.concurrent.thread

/**
 * Database engine implementation using Room.
 *
 * @author  sercant
 * @date 19/02/2018
 */
class RoomEngine(private val context: Context) : Engine {

    override fun bulkInsertAsync(events: Array<AccelerometerEvent>) {
        thread {
            val db = AccelerometerRoomDatabase.getInstance(context)
            val data = arrayListOf<AccelerometerData>()
            events.forEach { event: AccelerometerEvent ->
                data.add(AccelerometerData(event))
            }
            db!!.AccelerometerDataDao().insertAll(data.toTypedArray())
        }
    }

    override fun saveDeviceAsync(device: AccelerometerDevice) {
        // TODO (sercant): implement
    }
}