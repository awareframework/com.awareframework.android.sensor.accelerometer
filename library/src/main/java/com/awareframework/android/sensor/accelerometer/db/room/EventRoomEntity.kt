package com.awareframework.android.sensor.accelerometer.db.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.awareframework.android.sensor.accelerometer.model.AccelerometerEvent

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
            event.timezone,
            event.deviceId,
            event.x,
            event.y,
            event.z,
            event.eventTimestamp,
            event.accuracy,
            event.label,
            event.os,
            event.jsonVersion
    ) {
        this.id = id
    }
}