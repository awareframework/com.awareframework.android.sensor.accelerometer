package com.aware.sensor.accelerometer.db

import android.content.Context
import com.aware.sensor.accelerometer.model.AccelerometerDevice
import com.aware.sensor.accelerometer.model.AccelerometerEvent


/**
 * Base interface for implementing database engines.
 *
 * @author  sercant
 * @date 15/02/2018
 */
abstract class Engine(private val context: Context, private val encryptionKey: String?) {

    enum class DatabaseType {
        ROOM,
        NONE
    }

    class Builder(val context: Context) {
        private var type: DatabaseType = DatabaseType.NONE
        private var encryptionKey: String? = null

        fun setDatabaseType(type: DatabaseType) = apply { this.type = type }
        fun setEncryptionKey(encryptionKey: String?) = apply { this.encryptionKey = encryptionKey }

        fun build(): Engine? {
            return when (type) {
                DatabaseType.ROOM -> RoomEngine(context, encryptionKey)
                DatabaseType.NONE -> null
            }
        }
    }

    abstract fun bulkInsertAsync(events: Array<AccelerometerEvent>)
    abstract fun saveDeviceAsync(device: AccelerometerDevice)
    abstract fun destroy()
}

