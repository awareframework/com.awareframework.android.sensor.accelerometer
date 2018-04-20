package com.awareframework.android.sensor.accelerometer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.awareframework.android.core.model.SensorObserver
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Math.abs
import java.lang.Thread.sleep

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class SensorTest {

    private lateinit var sensor: Accelerometer

    private var interval: Int = 0

    private val onDataReceived: SensorObserver = object : SensorObserver {
        private var lastTimestamp: Long = System.currentTimeMillis()
        private var dataCount: Int = 0

        override fun onDataChanged(type: String, data: Any?, error: Any?) {
            val currentTimestamp = System.currentTimeMillis()
            if (currentTimestamp < lastTimestamp + 1000) {
                dataCount++
            } else {
                interval = dataCount
                dataCount = 0
                lastTimestamp = currentTimestamp
            }
        }
    }

    @Before
    fun init() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        sensor = Accelerometer.Builder(appContext)
                .setDebug(true)
                .setSensorObserver(onDataReceived)
                .build()
    }

    @Test
    fun testSensorCollectsDataAtGivenInterval() {
        sensor.start()
        val dataPoints = ArrayList<Int>()

        // wait until interval determined
        while (interval == 0) {
            sleep(500)
        }

        for (i in 0..10) {
            dataPoints.add(sensor.currentInterval)
            sleep(1000)
        }

        val diff: Double = abs(sensor.config.interval.toDouble() - dataPoints.average())

        assertTrue(diff < 1.0)

        sensor.stop()
    }

    @Test
    fun testSensorSync() {
        val appContext = InstrumentationRegistry.getTargetContext()
        var syncReceived = false

        sensor.start()
        sleep(1000)


        appContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                syncReceived = true
            }
        }, IntentFilter(Accelerometer.ACTION_AWARE_ACCELEROMETER_SYNC_SENT))

        sensor.sync(true)
        sleep(1000)

        assertTrue(syncReceived)

        sensor.stop()
    }

    @After
    fun tearDown() {
        sensor.stop()
    }
}
