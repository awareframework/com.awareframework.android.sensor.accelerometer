package com.awareframework.android.sensor.accelerometer

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.awareframework.android.core.manager.DbSyncManager
import com.awareframework.android.core.model.SensorObserver
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class SensorTest {

    private var sensor: Accelerometer? = null
    private var wasAbleToLogEvents = false

    @Before
    @Throws(Exception::class)
    fun init() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        sensor = Accelerometer.Builder(appContext).build()

        sensor = Accelerometer.Builder(appContext)
                .setDebug(true)
                .setSensorObserver(object: SensorObserver {
                    override fun onDataChanged(type: String, data: Any?, error: Any?) {
                        wasAbleToLogEvents = true
                    }
                })
                .build()

        val syncManager = DbSyncManager.Builder(appContext)
                .setDebug(true)
                .setSyncInterval(0.1f)
                .setBatteryChargingOnly(false)
                .setWifiOnly(false)
                .build()

        syncManager.start()
    }

    @Test
    @Throws(Exception::class)
    fun testStopSensor() {
        // can we run a sensor after stopping it
        sensor!!.stop()
        Thread.sleep(1000)
        sensor!!.start()
        Thread.sleep(1000)

        sensor!!.stop()
        Thread.sleep(1000)

        wasAbleToLogEvents = false
        Thread.sleep(1000)
        assertFalse(wasAbleToLogEvents)


        wasAbleToLogEvents = false
        sensor!!.start()
        Thread.sleep(10000)
        assertTrue(wasAbleToLogEvents)
        sensor!!.stop()
    }

    @Test
    @Throws(Exception::class)
    fun testStartSensor() {
        // can we log any events?
        wasAbleToLogEvents = false

        sensor!!.start()
        Thread.sleep(10000)
        assertTrue(wasAbleToLogEvents)
        sensor!!.stop()
    }

    @After
    fun tearDown() {
        sensor = null
    }
}
