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

  /**
   * Reads the entire file as a UTF-8 string.
   *
   * @param path the path to the file
   * @return the file content as a string
   * @throws IOException if an I/O error occurs
   */
  public static String readFile(Path path) throws IOException {
    return Files.readString(path);
  }

  /**
   * Creates a temporary directory with the given prefix and returns its path.
   *
   * @param prefix the prefix string to be used in generating the directory's name
   * @return the path to the newly created directory
   * @throws IOException if an I/O error occurs
   */
  public static Path createTempDir(String prefix) throws IOException {
    return Files.createTempDirectory(prefix);
  }

  /**
   * Serializes an object as JSON to the given file path.
   *
   * @param path the path to the file
   * @param value the object to serialize
   * @throws IOException if an I/O error occurs
   */
  public static void writeJson(Path path, Object value) throws IOException {
    MAPPER.writeValue(path.toFile(), value);
  }

  /**
   * Deletes every file and sub-directory in the given directory (does not delete the directory
   * itself).
   *
   * @param dir the directory to clean
   * @throws IOException if an I/O error occurs
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
