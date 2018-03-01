package com.aware.sensor.accelerometer.model

import com.google.gson.Gson

/**
 * Holds the data of the sensor events
 *
 * @author  sercant
 * @date 17/02/2018
 */
data class AccelerometerEvent(
        var timestamp: Long,
        var event_timestamp: Long,
        var timezone: Int,
        var device_id: String,
        var x: Float,
        var y: Float,
        var z: Float,
        var accuracy: Int,
        var label: String,
        var os: String,
        var json_version: Int
) {
    constructor() : this(0, 0, 0, "", 0f, 0f, 0f,
            0, "", "android", 1)

    fun toJson(): String {
        return Gson().toJson(this)
    }
}