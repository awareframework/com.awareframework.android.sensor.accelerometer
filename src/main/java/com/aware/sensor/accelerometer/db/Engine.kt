package com.aware.sensor.accelerometer.db

import android.util.Log
import com.aware.sensor.accelerometer.model.AccelerometerDevice
import com.aware.sensor.accelerometer.model.AccelerometerEvent

/**
 * Created by alkila on 15/02/2018.
 */
class Engine {

    fun connect() {

    }

    companion object {
        fun getDefaultEngine(): Engine {
            return Engine()
        }
    }

    fun bulkInsertAsync(events: Array<AccelerometerEvent>) {
        Log.d("Aclm-Engine", events.toString())
    }

    fun saveDeviceAsync(device: AccelerometerDevice) {
        Log.d("Aclm-Engine", device.toString())
    }
}