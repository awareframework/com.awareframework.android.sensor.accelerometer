Copyright (c) 2014 AWARE Mobile Context Instrumentation Middleware/Framework (http://www.awareframework.com)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

# AWARE Accelerometer

[![jitpack-badge](https://jitpack.io/v/awareframework/com.aware.android.sensor.accelerometer.svg)](https://jitpack.io/#awareframework/com.aware.android.sensor.accelerometer)

The accelerometer measures the acceleration applied to the sensor built-in into the device, including the force of gravity. In other words, the force of gravity is always influencing the measured acceleration, thus when the device is sitting on a table, the accelerometer reads the acceleration of gravity: 9.81 m/s². Similarly, if the phone is in free-fall towards the ground, the accelerometer reads: 0 m/s².

![Sensor axes](http://www.awareframework.com/wp-content/uploads/2015/01/axis_device.png)

The coordinate-system is defined relative to the screen of the phone in its default orientation (facing the user). The axis are not swapped when the device’s screen orientation changes. The X axis is horizontal and points to the right, the Y axis is vertical and points up and the Z axis points towards the outside of the front face of the screen. In this system, coordinates behind the screen have negative Z axis. Also, the natural orientation of a device is not always portrait, as the natural orientation for many tablet devices is landscape. For more information, check the official [Android’s Sensor Coordinate System](http://developer.android.com/guide/topics/sensors/sensors_overview.html#sensors-coords) documentation.

## Public functions

Here we denote lower-case for the instances of the accelerometer and upper-case for the static function access.

### Accelerometer

This is the main interaction controller with the sensor for the programmers.

+ `start()`: Starts the accelerometer sensor with the prebuilt configuration.

+ `stop()`: Stops the accelerometer service.

+ `sync(force: Boolean)`: sends sync signal to the sensor. `force` determines if the signal should go through the configured `SyncManager` or directly to the database `Engine`.

+ `isEnabled(): Boolean`: returns the state information about if the sensor is configured to be enabled.

+ `enable()`: enables the sensor in the configuration.

+ `disable()`: disables the sensor in the configuration.

+ `currentInterval`: holds the current data interval rate for the running service in hertz. 

### Accelerometer.Builder

A builder class for building an instance of an accelerometer controller.

+ `setLabel(label: String)`: collected data will be labeled accordingly. (default = "")

+ `setDebug(debug: Boolean)`: enable/disable logging to `Logcat`. (default = false)

+ `setDatabaseHost(host: String)`: host for syncing the database. (default = null)

+ `setDatabaseEncryptionKey(key: String)`: Encryption key for the database. (default = no encryption)

+ `setDatabaseHost(host: String)`: Host for syncing the database. (default = null)

+ `setDatabaseType(type: Engine.DatabaseType)`: Which db engine to use for saving data. (default = NONE)

+ `setDatabasePath(path: String)`: Path of the database.

+ `setInterval(interval: Int)`: Sample count per second in hertz. (*fastest* default = 0)

+ `setThreshold(threshold: Float)`: Threshold magnitude between each data collection. (default = 0)

+ `setPeriod(period: Float)`: Period of database saves in minutes. (default = 1.0)

+ `setSensorObserver(sensorObserver: SensorObserver)`: Callback for live data updates.

+ `setDeviceId(deviceId: String)`: Id of the device that will be associated with the events and the sensor. (default = "")

+ `setWakeLock(wakeLock: Boolean)`: Enable/disable wakelock, permissions needs to be handled by the client.

### Broadcasts

+ `Accelerometer.ACTION_AWARE_ACCELEROMETER_LABEL`: Send this broadcast to assign a label to the ongoing recording of data with the field intent extra as `Accelerometer.EXTRA_AWARE_ACCELEROMETER_LABEL`.

### Data Representations

#### Accelerometer Device

| Field | Type | Description |
| --- | --- | --- |
| maxRange | Float | Maximum sensor value possible |
| minDelay | Float | Minimum sampling delay in microseconds |
| name | String | Sensor’s name |
| power | Float | Sensor’s power drain in mA |
| resolution | Float | Sensor’s resolution in sensor’s units |
| type | String | Sensor’s type |
| vendor | String | Sensor’s manufacturer |
| version | String | Sensor’s version number |
| deviceId | String | AWARE device UUID |
| timestamp | Long | unixtime milliseconds since 1970 |
| timezone | Int | Timezone of the device |
| os | String | Operating system of the device (ex. android) |

#### Accelerometer Event

| Field | Type | Description |
| --- | --- | --- |
| x | Float | value of X axis |
| y | Float | value of Y axis |
| z | Float | value of Z axis |
| accuracy | Int | Sensor’s accuracy level (see [SensorManager](http://developer.android.com/reference/android/hardware/SensorManager.html)) |
| eventTimestamp | Long | unix timestamp of the actual event timestamp |
| label | String | Customizable label. Useful for data calibration or traceability |
| deviceId | String | AWARE device UUID |
| timestamp | Long | unixtime milliseconds since 1970 |
| timezone | Int | Timezone of the device |
| os | String | Operating system of the device (ex. android) |

## Example usage

In your root `build.gradle` add the jitpack repository.
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
        maven { url "https://s3.amazonaws.com/repo.commonsware.com" }
    }
}
```
In your app `build.gradle` add the dependency to the accelerometer.

```gradle
dependencies {
    compile 'com.github.awareframework:com.aware.android.sensor.accelerometer:master-SNAPSHOT'
}
```

In your source code:

```kotlin
val accelerometer = Accelerometer.Builder(this)
                    .setDebug(true)
                    .setDatabaseType(Engine.DatabaseType.ROOM)
                    .setDatabasePath("database-name")
                    .setDatabaseHost("https://node.awareframework.com/insert")
                    .setDeviceId(deviceId)
                    .setSensorObserver { type, data, error ->
                        if (error != null) {
                            Log.e("Test", error.toString())
                        } else when (type) {
                            AccelerometerEvent.TYPE -> {
                                val event = data as AccelerometerEvent
                                
                                // your code here
                                Log.d("Test", event.toJson())
                            }
                        }
                    }
                    .build()

accelerometer.start()

// to enable syncing to the db host automatically use
val syncManager = DbSyncManager.Builder(this)
                 .setDebug(true)
                 .setBatteryChargingOnly(true)
                 .setWifiOnly(true)
                 .setSyncInterval(1f)
                 .build()

syncManager.start()
```