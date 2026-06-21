package com.qaframework.tests;

import com.qaframework.base.BaseTest;
import com.qaframework.data.models.User;
import com.qaframework.data.providers.UserDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Example demonstrating data-driven testing using TestNG and JSON fixtures.
 *
 * <p>This test verifies that data successfully flows from {@code users.json} into the test context
 * via the {@code UserDataProvider}.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class DataDrivenTest extends BaseTest {

  private static final Logger log = LoggerFactory.getLogger(DataDrivenTest.class);

  /**
   * TestNG will execute this method multiple times, once for each user in users.json.
   *
   * @param user the injected User model
   */
  @Test(
      description = "Demonstrates injecting test data from users.json",
      dataProvider = "userData",
      dataProviderClass = UserDataProvider.class)
  public void testUserRolesFromFixture(User user) {
    log.info("Testing user: {} with role: {}", user.username(), user.role());

    Assert.assertNotNull(user.username(), "Username should not be null");
    Assert.assertTrue(
        user.role().equals("admin")
            || user.role().equals("user")
            || user.role().equals("moderator"),
        "User role must be valid");
  }
}
