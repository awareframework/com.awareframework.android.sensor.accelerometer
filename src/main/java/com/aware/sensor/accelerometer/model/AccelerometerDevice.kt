package com.aware.sensor.accelerometer.model

/**
 * Holds the information about
 * the accelerometer sensor of the device
 *
 * @author  sercant
 * @date 17/02/2018
 */
data class AccelerometerDevice(
        var device_id: String,
        var timestamp: Long,
        var double_sensor_maximum_range: Float,
        var double_sensor_minimum_delay: Float,
        var sensor_name: String,
        var double_sensor_power_ma: Float,
        var double_sensor_resolution: Float,
        var sensor_type: String,
        var sensor_vendor: String,
        var sensor_version: String
) {
    constructor() : this("", 0, 0f, 0f, "", 0f,
            0f, "", "", "")
}