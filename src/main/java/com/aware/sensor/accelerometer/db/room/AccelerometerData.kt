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
class AccelerometerData( @PrimaryKey(autoGenerate = true) var id: Long?,
                         @ColumnInfo(name = "timestamp") var timestamp: Long,
                         @ColumnInfo(name = "event_timestamp") var event_timestamp: Long,
                         @ColumnInfo(name = "timezone") var timezone: Int,
                         @ColumnInfo(name = "device_id") var device_id: String,
                         @ColumnInfo(name = "x") var x: Float,
                         @ColumnInfo(name = "y") var y: Float,
                         @ColumnInfo(name = "z") var z: Float,
                         @ColumnInfo(name = "accuracy") var accuracy: Int,
                         @ColumnInfo(name = "label") var label: String,
                         @ColumnInfo(name = "os") var os: String,
                         @ColumnInfo(name = "json_version") var json_version: Int
) {

    constructor() : this(
            null,
            0,
            0,
            0,
            "",
            0f,
            0f,
            0f,
            0,
            "",
            "android",
            0
    )

    constructor(event: AccelerometerEvent) : this(
            null,
            event.timestamp,
            event.event_timestamp,
            event.timezone,
            event.device_id,
            event.x,
            event.y,
            event.z,
            event.accuracy,
            event.label,
            event.os,
            event.json_version
    )
}