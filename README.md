# AWARE Accelerometer

[![jitpack-badge](https://jitpack.io/v/awareframework/com.aware.android.sensor.accelerometer.svg)](https://jitpack.io/#awareframework/com.aware.android.sensor.accelerometer)

The accelerometer measures the acceleration applied to the sensor built-in into the device, **including** the force of gravity. In other words, the force of gravity is always influencing the measured acceleration, thus when the device is sitting on a table, the accelerometer reads the acceleration of gravity: 9.81 m/s². Similarly, if the phone is in free-fall towards the ground, the accelerometer reads: 0 m/s².

![Sensor axes](http://www.awareframework.com/wp-content/uploads/2015/01/axis_device.png)

The coordinate-system is defined relative to the screen of the phone in its default orientation (facing the user). **The axis are not swapped when the device’s screen orientation changes.** The X axis is horizontal and points to the right, the Y axis is vertical and points up and the Z axis points towards the outside of the front face of the screen. In this system, coordinates behind the screen have negative Z axis. Also, the **natural orientation** of a device **is not always portrait**, as the natural orientation for many **tablet devices is landscape**. For more information, check the official [Android’s Sensor Coordinate System][3] documentation.

## Public functions

### AccelerometerSensor

+ `start(context: Context, config: AccelerometerSensor.Config?)`: Starts the accelerometer sensor with the optional configuration.
+ `stop(context: Context)`: Stops the service.
+ `currentInterval`: Data collection rate per second. (e.g. 5 samples per second)

### AccelerometerSensor.Config

Class to hold the configuration of the sensor.

#### Fields

+ `sensorObserver: AccelerometerSensor.Observer`: Callback for live data updates.
+ `interval: Int`: Data samples to collect per second. (default = 5)
+ `period: Float`: Period to save data in minutes. (default = 1)
+ `threshold: Double`: If set, do not record consecutive points if change in value is less than the set value.
+ `enabled: Boolean` Sensor is enabled or not. (default = false)
+ `debug: Boolean` enable/disable logging to `Logcat`. (default = false)
+ `label: String` Label for the data. (default = "")
+ `deviceId: String` Id of the device that will be associated with the events and the sensor. (default = "")
+ `dbEncryptionKey` Encryption key for the database. (default =String? = null)
+ `dbType: Engine` Which db engine to use for saving data. (default = `Engine.DatabaseType.NONE`)
+ `dbPath: String` Path of the database. (default = "aware_accelerometer")
+ `dbHost: String` Host for syncing the database. (Defult = `null`)

## Broadcasts

### Fired Broadcasts

+ `AccelerometerSensor.ACTION_AWARE_ACCELEROMETER` fired when accelerometer saved data to db after the period ends.

### Received Broadcasts

+ `AccelerometerSensor.ACTION_AWARE_ACCELEROMETER_START`: received broadcast to start the sensor.
+ `AccelerometerSensor.ACTION_AWARE_ACCELEROMETER_STOP`: received broadcast to stop the sensor.
+ `AccelerometerSensor.ACTION_AWARE_ACCELEROMETER_SYNC`: received broadcast to send sync attempt to the host.
+ `AccelerometerSensor.ACTION_AWARE_ACCELEROMETER_SET_LABEL`: received broadcast to set the data label. Label is expected in the `AccelerometerSensor.EXTRA_LABEL` field of the intent extras.

## Data Representations

### Accelerometer Sensor

Contains the hardware sensor capabilities in the mobile device.

| Field      | Type   | Description                                                     |
| ---------- | ------ | --------------------------------------------------------------- |
| maxRange   | Float  | Maximum sensor value possible                                   |
| minDelay   | Float  | Minimum sampling delay in microseconds                          |
| name       | String | Sensor’s name                                                  |
| power      | Float  | Sensor’s power drain in mA                                     |
| resolution | Float  | Sensor’s resolution in sensor’s units                         |
| type       | String | Sensor’s type                                                  |
| vendor     | String | Sensor’s vendor                                                |
| version    | String | Sensor’s version                                               |
| deviceId   | String | AWARE device UUID                                               |
| label      | String | Customizable label. Useful for data calibration or traceability |
| timestamp  | Long   | unixtime milliseconds since 1970                                |
| timezone   | Int    | [Raw timezone offset][1] of the device                          |
| os         | String | Operating system of the device (ex. android)                    |

### Accelerometer Data

Contains the raw sensor data.

| Field     | Type   | Description                                                         |
| --------- | ------ | ------------------------------------------------------------------- |
| x         | Float  | the acceleration force along the x axis, including gravity, in m/s² |
| y         | Float  | the acceleration force along the y axis, including gravity, in m/s² |
| z         | Float  | the acceleration force along the z axis, including gravity, in m/s² |
| accuracy  | Int    | Sensor’s accuracy level (see [SensorManager][2])                   |
| label     | String | Customizable label. Useful for data calibration or traceability     |
| deviceId  | String | AWARE device UUID                                                   |
| label     | String | Customizable label. Useful for data calibration or traceability     |
| timestamp | Long   | unixtime milliseconds since 1970                                    |
| timezone  | Int    | [Raw timezone offset][1] of the device                              |
| os        | String | Operating system of the device (ex. android)                        |

## Example usage

```kotlin
// To start the service.
AccelerometerSensor.start(appContext, AccelerometerSensor.Config().apply {
    sensorObserver = object : AccelerometerSensor.Observer {
        override fun onDataChanged(data: AccelerometerData) {
            // your code here...
        }
    }
    dbType = Engine.DatabaseType.ROOM
    debug = true
    // more configuration...
})

// To stop the service
AccelerometerSensor.stop(appContext)
```

## License

Copyright (c) 2018 AWARE Mobile Context Instrumentation Middleware/Framework (http://www.awareframework.com)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

[1]: https://developer.android.com/reference/java/util/TimeZone#getRawOffset()
[2]: http://developer.android.com/reference/android/hardware/SensorManager.html
[3]: http://developer.android.com/guide/topics/sensors/sensors_overview.html#sensors-coords