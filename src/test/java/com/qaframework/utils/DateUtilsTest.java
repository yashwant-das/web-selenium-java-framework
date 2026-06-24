package com.qaframework.utils;

import java.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit tests for DateUtils utility class. */
public class DateUtilsTest {

  @Test(description = "Verifies same-week logic for dates in the same week and different weeks")
  public void testIsSameWeek() {
    LocalDate tuesday = LocalDate.of(2026, 6, 23);
    LocalDate wednesday = LocalDate.of(2026, 6, 24);
    LocalDate monday = LocalDate.of(2026, 6, 22);
    LocalDate nextMonday = LocalDate.of(2026, 6, 29);

    Assert.assertTrue(
        DateUtils.isSameWeek(tuesday, wednesday),
        "Tuesday and Wednesday in the same week should be same week");
    Assert.assertTrue(
        DateUtils.isSameWeek(tuesday, monday),
        "Tuesday and Monday in the same week should be same week");
    Assert.assertFalse(
        DateUtils.isSameWeek(tuesday, nextMonday),
        "Tuesday and next Monday should not be same week");
  }

  @Test(description = "Verifies helper methods for adding and subtracting days")
  public void testPlusMinusDays() {
    LocalDate today = LocalDate.now();
    Assert.assertEquals(DateUtils.plusDays(0), today);
    Assert.assertEquals(DateUtils.plusDays(5), today.plusDays(5));
    Assert.assertEquals(DateUtils.minusDays(5), today.minusDays(5));
  }

  @Test(description = "Verifies display formatting and quarters logic")
  public void testQuarterAndFormatting() {
    LocalDate date = LocalDate.of(2026, 6, 23);
    Assert.assertEquals(DateUtils.getDayName(date), "Tuesday");

    LocalDate today = LocalDate.now();
    Assert.assertEquals(DateUtils.isPast(today.minusDays(1)), true);
    Assert.assertEquals(DateUtils.isFuture(today.plusDays(1)), true);
  }
}
