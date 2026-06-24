package com.qaframework.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

/**
 * Utility methods for date and time operations used in test automation.
 *
 * <p>Provides ISO formatting, local datetime generation, Date conversion helpers, and relative date
 * calculations.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public final class DateUtils {

  private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private DateUtils() {
    throw new UnsupportedOperationException("Utility class; no instances.");
  }

  /** Returns the current date as {@code yyyy-MM-dd}. */
  public static String getToday() {
    return LocalDate.now().format(ISO_DATE);
  }

  /** Returns the current datetime with milliseconds as {@code yyyy-MM-DDTHH:mm:ss.SSS}. */
  public static String nowWithMillis() {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    return LocalDateTime.now().format(fmt);
  }

  /**
   * Formats a {@code java.util.Date} as {@code yyyy-MM-dd}.
   *
   * @param date the date to format
   * @return the formatted date string
   */
  public static String formatDate(Date date) {
    return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
  }

  /**
   * Converts a {@code LocalDate} to the start of that day as {@code LocalDateTime}.
   *
   * @param date the date to convert
   * @return the resulting datetime
   */
  public static LocalDateTime toLocalDate(LocalDate date) {
    return date.atStartOfDay();
  }

  /**
   * Returns today plus N days.
   *
   * @param n the number of days to add
   * @return the resulting date
   */
  public static LocalDate plusDays(int n) {
    return LocalDate.now().plusDays(n);
  }

  /**
   * Returns today minus N days.
   *
   * @param n the number of days to subtract
   * @return the resulting date
   */
  public static LocalDate minusDays(int n) {
    return LocalDate.now().minusDays(n);
  }

  /**
   * Formats a {@code Date} as {@code dd/MM/yyyy}.
   *
   * @param date the date to format
   * @return the formatted string
   */
  public static String toDisplayFormat(Date date) {
    return new java.text.SimpleDateFormat("dd/MM/yyyy").format(date);
  }

  /** Returns the current epoch millis as a string. */
  public static String getTimestamp() {
    return String.valueOf(System.currentTimeMillis());
  }

  /**
   * Returns {@code true} if the given date is before today.
   *
   * @param date the date to check
   * @return {@code true} if the date is in the past
   */
  public static boolean isPast(LocalDate date) {
    return date.isBefore(LocalDate.now());
  }

  /**
   * Returns {@code true} if the given date is after today.
   *
   * @param date the date to check
   * @return {@code true} if the date is in the future
   */
  public static boolean isFuture(LocalDate date) {
    return date.isAfter(LocalDate.now());
  }

  /**
   * Returns the day of week with the first character uppercased (e.g. "Monday").
   *
   * @param date the date from which to get the day name
   * @return the formatted day name
   */
  public static String getDayName(LocalDate date) {
    String name = date.getDayOfWeek().toString();
    return name.substring(0, 1).toUpperCase(Locale.ROOT)
        + name.substring(1).toLowerCase(Locale.ROOT);
  }

  /**
   * Returns a human-readable relative string such as "3 days ago" or "tomorrow".
   *
   * @param date the date to format
   * @return a relative time string
   */
  public static String toDisplayString(LocalDate date) {
    long days = ChronoUnit.DAYS.between(date, LocalDate.now());
    if (days == 0) return "today";
    if (days == 1) return "yesterday";
    if (days == -1) return "tomorrow";
    if (days > 0) return days + " days ago";
    return "in " + Math.abs(days) + " days";
  }

  /**
   * Returns a random date between the two boundaries (start inclusive, end exclusive).
   *
   * @param start the inclusive start date
   * @param end the exclusive end date
   * @return a random date between the boundaries
   */
  public static LocalDate between(LocalDate start, LocalDate end) {
    long days = ChronoUnit.DAYS.between(start, end);
    if (days <= 0) return start;
    int randomDays = (int) (Math.random() * days);
    return start.plusDays(randomDays);
  }

  /**
   * Returns {@code true} if both dates fall within the same Monday-Sunday week.
   *
   * @param d1 the first date
   * @param d2 the second date
   * @return {@code true} if both dates are in the same week
   */
  public static boolean isSameWeek(LocalDate d1, LocalDate d2) {
    return d1.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
        .isEqual(d2.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)));
  }

  /** Returns the first day of the current month. */
  public static LocalDate firstDayOfMonth() {
    return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
  }

  /** Returns the last day of the current month. */
  public static LocalDate lastDayOfMonth() {
    return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
  }

  /** Returns the current quarter (1-4). */
  public static int getCurrentQuarter() {
    return (LocalDate.now().getMonthValue() - 1) / 3 + 1;
  }

  /**
   * Returns {@code true} if the given year is a leap year.
   *
   * @param year the year to check
   * @return {@code true} if the year is a leap year
   */
  public static boolean isLeapYear(int year) {
    return java.time.Year.isLeap(year);
  }

  /**
   * Returns the number of days between two dates.
   *
   * @param start the start date
   * @param end the end date
   * @return the number of days between the dates
   */
  public static long getDaysBetween(LocalDate start, LocalDate end) {
    return ChronoUnit.DAYS.between(start, end);
  }
}
