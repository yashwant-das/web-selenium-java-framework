package com.qaframework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/** File-system helpers used in test automation. */
public final class FileUtils {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private FileUtils() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  /** Reads the entire file as a UTF-8 string. */
  public static String readFile(Path path) throws IOException {
    return Files.readString(path);
  }

  /** Creates a temporary directory with the given prefix and returns its path. */
  public static Path createTempDir(String prefix) throws IOException {
    return Files.createTempDirectory(prefix);
  }

  /** Serializes an object as JSON to the given file path. */
  public static void writeJson(Path path, Object value) throws IOException {
    MAPPER.writeValue(path.toFile(), value);
  }

  /**
   * Deletes every file and sub-directory in the given directory (does not delete the directory
   * itself).
   */
  public static void cleanDirectory(Path dir) throws IOException {
    if (!Files.exists(dir)) return;
    try (Stream<Path> stream = Files.list(dir)) {
      stream.forEachOrdered(
          p -> {
            try {
              Files.deleteIfExists(p);
            } catch (IOException ignored) {
            }
          });
    }
  }
}
