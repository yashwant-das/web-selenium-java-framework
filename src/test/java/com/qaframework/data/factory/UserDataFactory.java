package com.qaframework.data.factory;

import com.github.javafaker.Faker;
import com.qaframework.data.model.UserData;

/**
 * Factory class utilizing Java Faker to generate randomized {@link UserData} objects at runtime.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class UserDataFactory {

  private static final Faker faker = new Faker();

  private UserDataFactory() {
    throw new UnsupportedOperationException("Factory class; no instances.");
  }

  /**
   * Generates a completely randomized user.
   *
   * @return a random UserData instance
   */
  public static UserData createRandomUser() {
    int id = faker.number().randomDigitNotZero();
    String name = faker.name().fullName();
    String username = faker.name().username();
    String password = faker.internet().password();
    String email = faker.internet().emailAddress();
    String role = "user";
    return new UserData(id, name, username, password, email, role);
  }

  /**
   * Generates a configured admin user with valid the-internet credentials.
   *
   * @return an admin UserData instance
   */
  public static UserData createAdminUser() {
    int id = 1;
    String name = "Tom Smith";
    String username = "tomsmith";
    String password = "SuperSecretPassword!";
    String email = "tomsmith@theinternet.com";
    String role = "admin";
    return new UserData(id, name, username, password, email, role);
  }
}
