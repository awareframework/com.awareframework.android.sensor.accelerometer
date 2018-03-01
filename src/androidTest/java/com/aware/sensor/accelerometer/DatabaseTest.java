package com.aware.sensor.accelerometer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.aware.sensor.accelerometer.db.Engine;
import com.aware.sensor.accelerometer.model.AccelerometerEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    static final String ENCRYPTION_KEY = "custom_key";

    @Test
    public void initDatabases() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Engine engine = new Engine.Builder(appContext)
                .setDatabaseType(Engine.DatabaseType.NONE)
                .build();

        assertNull(engine);

        engine = new Engine.Builder(appContext)
                .setDatabaseName("test.db")
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .build();

        assertNotNull(engine);

        engine = new Engine.Builder(appContext)
                .setDatabaseName("test_encrypted.db")
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .setEncryptionKey(ENCRYPTION_KEY)
                .build();

        assertNotNull(engine);
    }

    @Test
    public void testInsertAll() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        // Create some default events
        ArrayList<AccelerometerEvent> events = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            events.add(new AccelerometerEvent());
        }

        final AccelerometerEvent[] data_buffer = new AccelerometerEvent[events.size()];
        events.toArray(data_buffer);

        Engine engine = new Engine.Builder(appContext)
                .setDatabaseName("test.db")
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .build();
        engine.clearData().join();
        engine.bulkInsertAsync(data_buffer).join();
        List<AccelerometerEvent> data = engine.getAll(AccelerometerEvent.class);
        assertEquals(data.size(), events.size());
        engine.destroy();

        Engine encryptedEngine = new Engine.Builder(appContext)
                .setDatabaseName("test_encrypted.db")
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .setEncryptionKey(ENCRYPTION_KEY)
                .build();
        encryptedEngine.clearData().join();
        encryptedEngine.bulkInsertAsync(data_buffer).join();
        List<AccelerometerEvent> data2 = encryptedEngine.getAll(AccelerometerEvent.class);
        assertEquals(data2.size(), events.size());
        encryptedEngine.destroy();
    }
}
