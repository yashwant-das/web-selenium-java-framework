package com.qaframework.data.providers;

import com.qaframework.data.models.User;
import com.qaframework.utils.FileUtils;
import com.qaframework.utils.JsonUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;

/**
 * Provides dynamic and static test data to TestNG tests.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class UserDataProvider {

  private static final Logger log = LoggerFactory.getLogger(UserDataProvider.class);
  private static final Path USERS_JSON_PATH =
      Paths.get("src", "test", "resources", "fixtures", "users.json");

  private UserDataProvider() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  /**
   * Reads users from JSON and feeds them to the test method one by one.
   *
   * @return a 2D array of User objects
   */
  @DataProvider(name = "userData")
  public static Object[][] provideUsers() {
    try {
      log.debug("Loading test data from {}", USERS_JSON_PATH);
      String json = FileUtils.readFile(USERS_JSON_PATH);
      User[] users = JsonUtils.toArray(json, User.class);

      Object[][] data = new Object[users.length][1];
      for (int i = 0; i < users.length; i++) {
        data[i][0] = users[i];
      }
      return data;
    } catch (IOException e) {
      log.error("Failed to load users.json", e);
      throw new RuntimeException("Test data load failure", e);
    }
  }
}
