package com.aware.sensor.accelerometer.db.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.aware.sensor.accelerometer.model.AccelerometerEvent


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
                             @ColumnInfo(name = "double_values_0") var double_values_0: Float,
                             @ColumnInfo(name = "double_values_1") var double_values_1: Float,
                             @ColumnInfo(name = "double_values_2") var double_values_2: Float,
                             @ColumnInfo(name = "accuracy") var accuracy: Int,
                             @ColumnInfo(name = "label") var label: String

) {
    constructor() : this(null, 0, "", 0f, 0f, 0f,
            0, "")

    constructor(event: AccelerometerEvent) : this(null, event.timestamp, event.device_id, event.double_values_0, event.double_values_1, event.double_values_1,
            event.accuracy, event.label)

}