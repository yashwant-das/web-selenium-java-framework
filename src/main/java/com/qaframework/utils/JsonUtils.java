package com.qaframework.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Map;

/** Static Jackson helpers for converting objects to/from JSON. */
public final class JsonUtils {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
  }

  private JsonUtils() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  /**
   * Serializes an object to a JSON string.
   *
   * @param value the object to serialize
   * @return the JSON string representation
   */
  public static String toJson(Object value) {
    try {
      return OBJECT_MAPPER.writeValueAsString(value);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Parses a JSON string into a {@code Map<String, Object>}.
   *
   * @param json the JSON string to parse
   * @return the parsed map
   */
  public static Map<String, Object> toMap(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Deserializes a JSON string into an array of the given component type.
   *
   * @param <T> the type of the array elements
   * @param json the JSON string to deserialize
   * @param elementClass the class of the array elements
   * @return the deserialized array
   */
  public static <T> T[] toArray(String json, Class<T> elementClass) {
    try {
      return OBJECT_MAPPER.readValue(
          json, OBJECT_MAPPER.getTypeFactory().constructArrayType(elementClass));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
