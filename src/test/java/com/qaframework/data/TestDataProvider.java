package com.qaframework.data;

import com.qaframework.data.model.UserData;
import org.testng.annotations.DataProvider;

/**
 * Centralized TestNG DataProvider that supplies static data and JSON-loaded data to tests.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class TestDataProvider {

  private TestDataProvider() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  /**
   * Provides credentials for login testing (valid, invalid, and empty username).
   *
   * @return a 2D array containing UserData object, success boolean, and expected error substring
   */
  @DataProvider(name = "loginCredentials")
  public static Object[][] provideLoginCredentials() {
    return new Object[][] {
      {
        new UserData(
            1,
            "Tom Smith",
            "tomsmith",
            "SuperSecretPassword!",
            "tomsmith@theinternet.com",
            "admin"),
        true,
        ""
      },
      {
        new UserData(
            2, "Invalid User", "invalidUser", "invalidPassword", "invalid@example.com", "user"),
        false,
        "Your username is invalid!"
      },
      {
        new UserData(3, "Empty Username", "", "SuperSecretPassword!", "empty@example.com", "user"),
        false,
        "Your username is invalid!"
      }
    };
  }

  /**
   * DataProvider that loads test users from an environment-specific users.json file.
   *
   * @return a 2D array of UserData objects
   */
  @DataProvider(name = "jsonUsers")
  public static Object[][] provideJsonUsers() {
    UserData[] users = TestDataLoader.load("users.json", UserData[].class);
    Object[][] data = new Object[users.length][1];
    for (int i = 0; i < users.length; i++) {
      data[i][0] = users[i];
    }
    return data;
  }
}
