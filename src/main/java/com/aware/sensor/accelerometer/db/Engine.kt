package com.aware.sensor.accelerometer.db

import android.content.Context
import android.util.Log
import com.aware.sensor.accelerometer.model.AccelerometerDevice
import com.aware.sensor.accelerometer.model.AccelerometerEvent


/**
 * Base interface for implementing database engines.
 *
 * @author  sercant
 * @date 15/02/2018
 */
interface Engine {

    enum class DatabaseType {
        ROOM
    }

//    fun connect(database: DatabaseType) {
////        when (database) {
////            DatabaseType.ROOM ->
////        }
//    }

    companion object {
        fun getDefaultEngine(context: Context): Engine {
            return RoomEngine(context)
        }
    }

    fun bulkInsertAsync(events: Array<AccelerometerEvent>) {
        Log.d("Aclm-Engine", events.toString())
    }

    fun saveDeviceAsync(device: AccelerometerDevice) {
        Log.d("Aclm-Engine", device.toString())
    }
}

