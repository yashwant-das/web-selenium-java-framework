package com.qaframework.data.factory;

import com.github.javafaker.Faker;
import com.qaframework.data.model.ProductData;

/**
 * Factory class utilizing Java Faker to generate randomized {@link ProductData} objects at runtime.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class ProductDataFactory {

  private static final Faker faker = new Faker();

  private ProductDataFactory() {
    throw new UnsupportedOperationException("Factory class; no instances.");
  }

  /**
   * Generates a completely randomized product.
   *
   * @return a random ProductData instance
   */
  public static ProductData createRandomProduct() {
    String name = faker.commerce().productName();
    String category = faker.commerce().department();
    double price = Double.parseDouble(faker.commerce().price());
    return new ProductData(name, category, price);
  }
}
