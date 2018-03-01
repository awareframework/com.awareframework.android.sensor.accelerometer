package com.aware.sensor.accelerometer.db

import android.content.Context
import com.aware.sensor.accelerometer.db.room.AccelerometerData
import com.aware.sensor.accelerometer.db.room.AccelerometerRoomDatabase
import com.aware.sensor.accelerometer.model.AccelerometerDevice
import com.aware.sensor.accelerometer.model.AccelerometerEvent
import net.sqlcipher.database.SQLiteException
import kotlin.concurrent.thread

/**
 * Database engine implementation using Room.
 *
 * @author  sercant
 * @date 19/02/2018
 */
class RoomEngine(context: Context, encryptionKey: String?, dbName: String) : Engine(context, encryptionKey, dbName) {

    // TODO (sercant): We should hold a reference to the database object here since this class is instantiated now.
    init {
        AccelerometerRoomDatabase.init(context, encryptionKey, dbName)
    }

    override fun bulkInsertAsync(events: Array<AccelerometerEvent>): Thread {
        return thread {
            try {
                val db = AccelerometerRoomDatabase.instance
                val data = arrayListOf<AccelerometerData>()
                events.forEach { event: AccelerometerEvent ->
                    data.add(AccelerometerData(event))
                }
                db!!.AccelerometerDataDao().insertAll(data.toTypedArray())
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun saveDeviceAsync(device: AccelerometerDevice): Thread {
        return thread {
            // TODO (sercant): implement
        }
    }

    override fun <T> getAll(klass: Class<T>): List<T>? {
        var result: List<T>? = null
        val db = AccelerometerRoomDatabase.instance

        when (klass) {
            AccelerometerEvent::class.java -> {
                // WARN (sercant): somehow this cast from db.room.AccelerometerData
                // to model.AccelerometerEvent is successful without any inheritance
                @Suppress("UNCHECKED_CAST")
                result = db!!.AccelerometerDataDao().getAll() as List<T>
            }
        }

        return result
    }

    override fun clearData(): Thread {
        return thread {
            try {
                val db = AccelerometerRoomDatabase.instance
                db!!.clearAllData()
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun destroy() {
        AccelerometerRoomDatabase.destroyInstance()
    }
}