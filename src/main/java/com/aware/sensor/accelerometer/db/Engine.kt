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
abstract class Engine(
        private val context: Context,
        private val encryptionKey: String?,
        private val dbName: String
) {

    enum class DatabaseType {
        ROOM,
        NONE
    }

    class Builder(val context: Context) {
        private var type: DatabaseType = DatabaseType.NONE
        private var encryptionKey: String? = null
        private var dbName: String = "aware_accelerometer.db"

        fun setDatabaseType(type: DatabaseType) = apply { this.type = type }
        fun setEncryptionKey(encryptionKey: String?) = apply { this.encryptionKey = encryptionKey }
        fun setDatabaseName(name: String) = apply { this.dbName = name }

        fun build(): Engine? {
            return when (type) {
                DatabaseType.ROOM -> RoomEngine(context, encryptionKey, dbName)
                DatabaseType.NONE -> null
            }
        }
    }

    abstract fun <T> getAll(klass: Class<T>): List<T>?

    abstract fun bulkInsertAsync(events: Array<AccelerometerEvent>): Thread
    abstract fun saveDeviceAsync(device: AccelerometerDevice): Thread
    abstract fun clearData(): Thread
    abstract fun destroy()
}

