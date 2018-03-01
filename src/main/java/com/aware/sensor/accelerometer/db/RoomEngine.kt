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
class RoomEngine(context: Context, encryptionKey: String?) : Engine(context, encryptionKey) {

    init {
        AccelerometerRoomDatabase.init(context, encryptionKey)
    }

    override fun bulkInsertAsync(events: Array<AccelerometerEvent>) {
        thread {
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

    override fun saveDeviceAsync(device: AccelerometerDevice) {
        // TODO (sercant): implement
    }

    override fun destroy() {
        AccelerometerRoomDatabase.destroyInstance()
    }
}