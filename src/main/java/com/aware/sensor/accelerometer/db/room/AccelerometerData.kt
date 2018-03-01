package com.aware.sensor.accelerometer.db.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.aware.sensor.accelerometer.model.AccelerometerEvent
import com.google.gson.Gson


/**
 * Room accelerometer data entity
 *
 * @author  sercant
 * @date 19/02/2018
 */
@Entity(tableName = "accelerometerData")
data class AccelerometerData(@PrimaryKey(autoGenerate = true) var id: Long?,
                             @ColumnInfo(name = "timestamp") var timestamp: Long,
                             @ColumnInfo(name = "device_id") var device_id: String,
                             @ColumnInfo(name = "x") var x: Float,
                             @ColumnInfo(name = "y") var y: Float,
                             @ColumnInfo(name = "z") var z: Float,
                             @ColumnInfo(name = "accuracy") var accuracy: Int,
                             @ColumnInfo(name = "label") var label: String

) {
    constructor() : this(null, 0, "", 0f, 0f, 0f,
            0, "")

    constructor(event: AccelerometerEvent) : this(null, event.timestamp, event.device_id, event.x, event.y, event.z,
            event.accuracy, event.label)


    fun toJson(): String {
        return Gson().toJson(this)
    }
}