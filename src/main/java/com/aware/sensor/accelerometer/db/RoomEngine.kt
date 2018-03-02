package com.aware.sensor.accelerometer.db

import android.content.Context
import com.aware.sensor.accelerometer.db.room.AccelerometerRoomDatabase
import com.aware.sensor.accelerometer.db.room.DeviceRoomEntity
import com.aware.sensor.accelerometer.db.room.EventRoomEntity
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
                val data = arrayListOf<EventRoomEntity>()
                events.forEach { event: AccelerometerEvent ->
                    data.add(EventRoomEntity(event = event))
                }
                db!!.AccelerometerEventDao().insertAll(data.toTypedArray())
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun saveDeviceAsync(device: AccelerometerDevice): Thread {
        return thread {
            try {
                val db = AccelerometerRoomDatabase.instance
                // TODO (sercant): We don't expect to have several sensors in one device right?
                val data = DeviceRoomEntity(0, device)
                db!!.AccelerometerDeviceDao().insert(data)
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun <T> getAll(klass: Class<T>): List<T>? {
        var result: List<T>? = null
        val db = AccelerometerRoomDatabase.instance


        when (klass) {
            AccelerometerEvent::class.java -> {
                @Suppress("UNCHECKED_CAST")
                result = db!!.AccelerometerEventDao().getAll() as List<T>
            }
            AccelerometerDevice::class.java -> {
                @Suppress("UNCHECKED_CAST")
                result = db!!.AccelerometerDeviceDao().getAll() as List<T>
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