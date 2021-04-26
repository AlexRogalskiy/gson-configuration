package io.gson.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 26.04.2021
 */
class OffsetTimeAdapterTests extends Assertions {

    static class User {

        private String name;
        private OffsetTime value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public OffsetTime getValue() {
            return value;
        }

        public void setValue(OffsetTime value) {
            this.value = value;
        }
    }

    private static final String CUSTOM_ISO = "HH-mm-ssxxx";
    private static final String CUSTOM_VALUE = "00-00-00+00:00";

    private static final OffsetTime VALUE_TIME = OffsetTime.of(LocalTime.MIN, ZoneOffset.UTC);
    private static final String VALUE = "00:00:00Z";

    private final Gson adapter = new GsonBuilder()
            .registerTypeAdapter(OffsetTime.class, new OffsetTimeAdapter())
            .create();

    private final Gson adapterCustom = new GsonBuilder()
            .registerTypeAdapter(OffsetTime.class, new OffsetTimeAdapter(DateTimeFormatter.ofPattern(CUSTOM_ISO)))
            .create();

    @Test
    void serializationIsValidForIso() {
        final User user = new User();
        user.setName("Bob");
        user.setValue(VALUE_TIME);

        final String json = adapter.toJson(user);
        assertNotNull(json);
        assertTrue(json.contains("\"value\":\"" + VALUE + "\""));
    }

    @Test
    void serializationIsValidForCustomFormatter() {
        final User user = new User();
        user.setName("Bob");
        user.setValue(VALUE_TIME);

        final String json = adapterCustom.toJson(user);
        assertNotNull(json);
        assertTrue(json.contains("\"value\":\"" + CUSTOM_VALUE + "\""));
    }

    @Test
    void deserializationIsValidForIso() {
        final String json = "{\"name\":\"Bob\",\"value\":\"" + VALUE + "\"}";

        final User user = adapter.fromJson(json, User.class);
        assertNotNull(user);
        assertEquals("Bob", user.getName());
        assertEquals(VALUE_TIME, user.getValue());
    }

    @Test
    void deserializationIsValidForIsoFails() {
        final String json = "{\"name\":\"Bob\",\"value\":\"1970-01-01 03:00\"}";

        try {
            adapter.fromJson(json, User.class);
            fail("Should not happen");
        } catch (JsonParseException e) {
            assertTrue(e.getCause() instanceof DateTimeException);
        }
    }

    @Test
    void deserializationIsValidForCustomFormatter() {
        final String json = "{\"name\":\"Bob\",\"value\":\"" + CUSTOM_VALUE + "\"}";

        final User user = adapterCustom.fromJson(json, User.class);
        assertNotNull(user);
        assertEquals("Bob", user.getName());
        assertEquals(VALUE_TIME, user.getValue());
    }
}