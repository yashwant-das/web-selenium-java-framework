package com.qaframework.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to load test data from JSON files under {@code src/test/resources/testdata/{env}/}.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class TestDataLoader {

  private static final Logger log = LoggerFactory.getLogger(TestDataLoader.class);
  private static final ObjectMapper mapper = new ObjectMapper();

  private TestDataLoader() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  /**
   * Loads a JSON file from the environment-specific test data folder.
   *
   * @param fileName the name of the JSON file
   * @param clazz the target class of the object or array
   * @param <T> the type parameter of the target class
   * @return the deserialized object/array of type {@code T}
   */
  public static <T> T load(String fileName, Class<T> clazz) {
    String env = System.getProperty("env", "local");
    Path path = Paths.get("src", "test", "resources", "testdata", env, fileName);
    log.info("Loading test data file from path: {}", path);
    try {
      String content = Files.readString(path);
      return mapper.readValue(content, clazz);
    } catch (IOException e) {
      log.error("Failed to load test data file: {}", fileName, e);
      throw new RuntimeException("Test data load failure for file: " + fileName, e);
    }
  }
}
