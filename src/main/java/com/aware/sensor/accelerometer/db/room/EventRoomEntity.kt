package com.aware.sensor.accelerometer.db.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.aware.sensor.accelerometer.model.AccelerometerEvent

/**
 * Room accelerometer data entity
 *
 * @author  sercant
 * @date 19/02/2018
 */


@Entity(tableName = "accelerometerEvent")
class EventRoomEntity : AccelerometerEvent {

    @PrimaryKey var id: Long? = null

    constructor() : super()

    constructor(id: Long? = null, event: AccelerometerEvent) : super(
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
    ) {
        this.id = id
    }
}