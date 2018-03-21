package com.awareframework.android.sensor.accelerometer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SensorTest {

    private Accelerometer sensor;
    private boolean wasAbleToLogEvents = false;

    @Before
    public void init() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        sensor = new Accelerometer.Builder(appContext).build();
        assertNotNull(sensor);

        sensor = new Accelerometer.Builder(appContext)
                .setDebug(true)
//                .setDeviceId(UUID.randomUUID().toString())
                .setSensorObserver((type, data, error) -> wasAbleToLogEvents = true)
//                .setBufferSize(300)
//                .setBufferTimeout(1000)
//                .setEnforceFrequency(true)
//                .setLabel("test")
//                .setThreshold(10)
//                .setWakeLock(false)
                .build();
    }

    @Test
    public void testStopSensor() throws Exception {
        // can we run a sensor after stopping it
        sensor.stop();
        Thread.sleep(1000);
        sensor.start();
        Thread.sleep(1000);

        sensor.stop();
        Thread.sleep(1000);

        wasAbleToLogEvents = false;
        Thread.sleep(1000);
        assertFalse(wasAbleToLogEvents);

        wasAbleToLogEvents = false;
        sensor.start();
        Thread.sleep(10000);
        assertTrue(wasAbleToLogEvents);
        sensor.stop();
    }

    @Test
    public void testStartSensor() throws Exception {
        // can we log any events?
        wasAbleToLogEvents = false;

        sensor.start();
        Thread.sleep(10000);
        assertTrue(wasAbleToLogEvents);
        sensor.stop();
    }

    @After
    public void tearDown() {
        sensor = null;
    }
}
