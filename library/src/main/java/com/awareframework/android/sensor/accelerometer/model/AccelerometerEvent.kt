package com.awareframework.android.sensor.accelerometer.model

import com.awareframework.android.sensor.core.model.AwareObject

/**
 * Holds the data of the sensor events
 *
 * @author  sercant
 * @date 17/02/2018
 */
open class AccelerometerEvent : AwareObject {

    var x: Float = 0f
    var y: Float = 0f
    var z: Float = 0f
    var eventTimestamp: Long = 0L
    var accuracy: Int = 0

    constructor() : super()

    constructor(
            x: Float,
            y: Float,
            z: Float,
            eventTimestamp: Long,
            accuracy: Int
    ) : super() {
        this.x = x
        this.y = y
        this.z = z
        this.eventTimestamp = eventTimestamp
        this.accuracy = accuracy
    }

    constructor(
            timestamp: Long,
            timezone: Int,
            deviceId: String,
            x: Float,
            y: Float,
            z: Float,
            eventTimestamp: Long,
            accuracy: Int,
            label: String,
            os: String,
            jsonVersion: Int
    ) : super(
            timestamp,
            timezone,
            deviceId,
            label,
            os,
            jsonVersion
    ) {
        this.x = x
        this.y = y
        this.z = z
        this.eventTimestamp = eventTimestamp
        this.accuracy = accuracy
    }
}