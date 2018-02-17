package com.aware.sensor.accelerometer.model

/**
 * Holds the data of the sensor events
 *
 * @author  sercant
 * @date 17/02/2018
 */
data class AccelerometerEvent(
        var timestamp: Long,
        var eventTimestamp: Long,
        var device_id: String,
        var double_values_0: Float,
        var double_values_1: Float,
        var double_values_2: Float,
        var accuracy: Int,
        var label: String
) {
    constructor() : this(0, 0, "", 0f, 0f, 0f,
            0, "")
}