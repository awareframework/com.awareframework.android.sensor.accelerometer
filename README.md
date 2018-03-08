Copyright (c) 2014 AWARE Mobile Context Instrumentation Middleware/Framework (http://www.awareframework.com)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

# AWARE Accelerometer

[![](https://jitpack.io/v/awareframework/com.aware.android.sensor.accelerometer.svg)](https://jitpack.io/#awareframework/com.aware.android.sensor.accelerometer)


**This repository is under development and not ready for use yet.**

This repository is a modularized version of the [AWARE](https://github.com/denzilferreira/aware-client) framework.

## Example usage

In your root `build.gradle` add the jitpack repository.
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
In your app `build.gradle` add the dependency to the accelerometer.

```gradle
dependencies {
    compile 'com.github.awareframework:com.aware.android.sensor.accelerometer:-SNAPSHOT'
}
```

In your source code:

```kotlin
val accelerometer: Accelerometer = Accelerometer.Builder(applicationContext)
        .setDebug(true)
        .setDeviceID(UUID.randomUUID().toString())
        .setSensorObserver(object : Accelerometer.SensorObserver {
            override fun onAccelerometerChanged(data: AccelerometerEvent) {
                Log.d("mSensorObserver", data.toString())
            }
        })
        .build()
accelerometer.start()
```