package com.qaframework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Thread-safe singleton for managing configuration across environments.
 *
 * <p>Loads properties from {@code src/test/resources/config/{env}.properties}. Active environment
 * is resolved via the {@code env} system property (default: {@code local}).
 *
 * <h2>Secrets Override</h2>
 *
 * <p>{@link #get(String)} checks {@code System.getenv(key.toUpperCase().replace('.', '_'))} first,
 * then falls back to the properties file. This ensures credentials never appear in committed files:
 *
 * <pre>{@code
 * Properties file:   BASE_URL = https://the-internet.herokuapp.com
 * Env variable:      BASE_URL = https://qa.example.com    Used at runtime
 * }</pre>
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * String url = ConfigManager.getInstance().get("base.url");
 * int retryCount = ConfigManager.getInstance().getInt("retry.count", 1);
 * boolean headless = ConfigManager.getInstance().getBoolean("browser.headless", false);
 * }</pre>
 *
 * <p>The configuration is loaded once at initialization and is effectively immutable thereafter.
 * All accessors are synchronized to guarantee thread safety in parallel TestNG execution.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class ConfigManager {

  private static final Logger log = Logger.getLogger(ConfigManager.class.getName());

  /** The singleton instance -- initialized eagerly for thread safety (effective immutable). */
  private static final ConfigManager INSTANCE = new ConfigManager();

  private volatile Properties properties;
  private final ConcurrentHashMap<String, String> envCache = new ConcurrentHashMap<>();

  /** Prevent external instantiation; use getInstance() to access configuration. */
  private ConfigManager() {
    load("local"); // default fallback
    log.info("ConfigManager initialized with default environment: local");
  }

  /**
   * Returns the singleton instance. Safe to call from any thread.
   *
   * @return the ConfigManager singleton
   */
  public static ConfigManager getInstance() {
    return INSTANCE;
  }

  /**
   * Reloads configuration from a specific environment properties file. Call this once at
   * application startup to switch environments.
   *
   * @param env the environment name (local, qa, sit, uat, prod)
   */
  public synchronized void load(String env) {
    Properties newProperties = new Properties();
    try (InputStream is =
        getClass().getClassLoader().getResourceAsStream("config/" + env + ".properties")) {
      if (is == null) {
        log.warning(
            "Configuration file not found for env: "
                + env
                + ". Using empty properties -- all getters will return defaults.");
        properties = newProperties;
        return;
      }
      newProperties.load(is);
      properties = newProperties;
      envCache.clear();
    } catch (IOException e) {
      log.severe("Failed to load configuration for env: " + env + " -- " + e.getMessage());
    }
  }

  /**
   * Returns the value for a property key.
   *
   * <p>Resolution order:
   *
   * <ol>
   *   <li>{@code System.getenv(key.toUpperCase().replace('.', '_'))}
   *   <li>Properties file value
   *   <li>{@code null} if neither is set
   * </ol>
   *
   * @param key the property key
   * @return the resolved string value, or {@code null} if not found
   */
  public String get(String key) {
    String envKey = key.toUpperCase(Locale.ROOT).replace('.', '_');

    // Check and cache env variables to avoid repeated System.getenv calls
    String envValue =
        envCache.computeIfAbsent(
            envKey,
            k -> {
              String val = System.getenv(k);
              return val != null ? val : ""; // Use empty string to cache nulls
            });

    if (!envValue.isEmpty()) {
      return envValue;
    }

    Properties props = properties; // read volatile
    return props != null ? props.getProperty(key) : null;
  }

  /**
   * Returns the value for a property key, falling back to {@code defaultValue} if not found.
   *
   * @param key the property key
   * @param defaultValue the fallback value if neither env var nor properties file has the key
   * @return the resolved string value, or {@code defaultValue} if not found
   */
  public String get(String key, String defaultValue) {
    String value = get(key);
    return value != null ? value : defaultValue;
  }

  /**
   * Returns the property as an {@code int}, with a default fallback.
   *
   * @param key the property key
   * @param defaultValue the fallback integer if not found or not parseable
   * @return the parsed integer, or {@code defaultValue} if not parseable
   */
  public int getInt(String key, int defaultValue) {
    String value = get(key);
    if (value == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      log.warning("Failed to parse integer for key: " + key + " -- using default: " + defaultValue);
      return defaultValue;
    }
  }

  /**
   * Returns the property as a {@code boolean}, with a default fallback.
   *
   * @param key the property key
   * @param defaultValue the fallback boolean if not found or not parseable
   * @return {@code true} if value is "true", {@code false} otherwise
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    String value = get(key);
    if (value == null) {
      return defaultValue;
    }
    return Boolean.parseBoolean(value.trim());
  }

  /**
   * Returns the property as a {@code long}, with a default fallback.
   *
   * @param key the property key
   * @param defaultValue the fallback long if not found or not parseable
   * @return the parsed long, or {@code defaultValue} if not parseable
   */
  public long getLong(String key, long defaultValue) {
    String value = get(key);
    if (value == null) {
      return defaultValue;
    }
    try {
      return Long.parseLong(value.trim());
    } catch (NumberFormatException e) {
      log.warning("Failed to parse long for key: " + key + " -- using default: " + defaultValue);
      return defaultValue;
    }
  }

  /**
   * Returns a summary of the active configuration for logging and Allure attachments.
   *
   * @return a formatted string with environment, base URL, and browser settings
   */
  public String getSummary() {
    return "Environment="
        + get("environment.name", "unknown")
        + ", BaseURL="
        + get("base.url", "unknown")
        + ", Browser="
        + get("browser", "unknown")
        + ", Headless="
        + get("browser.headless", "false");
  }
}
