/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.timeseries;

import javax.time.calendar.LocalDate;
import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.convention.frequency.Frequency;
import com.opengamma.financial.convention.frequency.PeriodFrequency;
import com.opengamma.financial.convention.frequency.SimpleFrequency;

/**
 * Factory to create schedules.
 */
public class ScheduleFactory {

  public static LocalDate[] getSchedule(final LocalDate startDate, final LocalDate endDate, final Frequency frequency, final boolean endOfMonth, final boolean fromEnd,
      final boolean generateRecursive) {
    Validate.notNull(startDate, "start date");
    Validate.notNull(endDate, "end date");
    Validate.notNull(frequency, "frequency");
    SimpleFrequency simple;
    if (frequency instanceof SimpleFrequency) {
      simple = (SimpleFrequency) frequency;
    } else if (frequency instanceof PeriodFrequency) {
      simple = ((PeriodFrequency) frequency).toSimpleFrequency();
    } else {
      throw new IllegalArgumentException("Can only handle SimpleFrequency and PeriodFrequency");
    }
    final int periodsPerYear = (int) simple.getPeriodsPerYear();
    return getSchedule(startDate, endDate, periodsPerYear, endOfMonth, fromEnd, generateRecursive);
  }

  public static LocalDate[] getSchedule(final LocalDate startDate, final LocalDate endDate, final int periodsPerYear, final boolean endOfMonth, final boolean fromEnd,
      final boolean generateRecursive) {
    Validate.notNull(startDate, "start date");
    Validate.notNull(endDate, "end date");
    Validate.isTrue(periodsPerYear > 0);
    LocalDate[] result = null;
    if (periodsPerYear == 1) {
      if (endOfMonth) {
        if (fromEnd && endDate.getDayOfMonth() == endDate.getMonthOfYear().getLastDayOfMonth(endDate.isLeapYear())) {
          result = ScheduleCalculatorFactory.ANNUAL_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else if (startDate.getDayOfMonth() == startDate.getMonthOfYear().getLastDayOfMonth(startDate.isLeapYear())) {
          result = ScheduleCalculatorFactory.ANNUAL_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else {
          result = ScheduleCalculatorFactory.ANNUAL_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        }
      } else {
        result = ScheduleCalculatorFactory.ANNUAL_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
      }
    } else if (periodsPerYear == 2) {
      if (endOfMonth) {
        if (fromEnd && endDate.getDayOfMonth() == endDate.getMonthOfYear().getLastDayOfMonth(endDate.isLeapYear())) {
          result = ScheduleCalculatorFactory.SEMI_ANNUAL_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else if (startDate.getDayOfMonth() == startDate.getMonthOfYear().getLastDayOfMonth(startDate.isLeapYear())) {
          result = ScheduleCalculatorFactory.SEMI_ANNUAL_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else {
          result = ScheduleCalculatorFactory.SEMI_ANNUAL_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        }
      } else {
        result = ScheduleCalculatorFactory.SEMI_ANNUAL_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
      }
    } else if (periodsPerYear == 4) {
      if (endOfMonth) {
        if (fromEnd && endDate.getDayOfMonth() == endDate.getMonthOfYear().getLastDayOfMonth(endDate.isLeapYear())) {
          result = ScheduleCalculatorFactory.QUARTERLY_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else if (startDate.getDayOfMonth() == startDate.getMonthOfYear().getLastDayOfMonth(startDate.isLeapYear())) {
          result = ScheduleCalculatorFactory.QUARTERLY_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else {
          result = ScheduleCalculatorFactory.QUARTERLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        }
      } else {
        result = ScheduleCalculatorFactory.QUARTERLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
      }
    } else if (periodsPerYear == 12) {
      if (endOfMonth) {
        if (fromEnd && endDate.getDayOfMonth() == endDate.getMonthOfYear().getLastDayOfMonth(endDate.isLeapYear())) {
          result = ScheduleCalculatorFactory.END_OF_MONTH_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else if (startDate.getDayOfMonth() == startDate.getMonthOfYear().getLastDayOfMonth(startDate.isLeapYear())) {
          result = ScheduleCalculatorFactory.END_OF_MONTH_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else {
          result = ScheduleCalculatorFactory.MONTHLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        }
      } else {
        result = ScheduleCalculatorFactory.MONTHLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
      }
    } else if (periodsPerYear == 52) {
      if (endOfMonth) {
        throw new IllegalArgumentException("Cannot get EOM series for weekly frequency");
      }
      result = ScheduleCalculatorFactory.WEEKLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
    } else if (periodsPerYear == 365 || periodsPerYear == 366) {
      if (endOfMonth) {
        throw new IllegalArgumentException("Cannot get EOM series for daily frequency");
      }
      result = ScheduleCalculatorFactory.DAILY_CALCULATOR.getSchedule(startDate, endDate);
    }
    Validate.notNull(result, "result");
    return result;
  }

  public static ZonedDateTime[] getSchedule(final ZonedDateTime startDate, final ZonedDateTime endDate, final Frequency frequency, final boolean endOfMonth, final boolean fromEnd,
      final boolean generateRecursive) {
    Validate.notNull(startDate, "start date");
    Validate.notNull(endDate, "end date");
    Validate.notNull(frequency, "frequency");
    SimpleFrequency simple;
    if (frequency instanceof SimpleFrequency) {
      simple = (SimpleFrequency) frequency;
    } else if (frequency instanceof PeriodFrequency) {
      simple = ((PeriodFrequency) frequency).toSimpleFrequency();
    } else {
      throw new IllegalArgumentException("Can only handle SimpleFrequency and PeriodFrequency");
    }
    final int periodsPerYear = (int) simple.getPeriodsPerYear();
    return getSchedule(startDate, endDate, periodsPerYear, endOfMonth, fromEnd, generateRecursive);
  }

  public static ZonedDateTime[] getSchedule(final ZonedDateTime startDate, final ZonedDateTime endDate, final int periodsPerYear, final boolean endOfMonth, final boolean fromEnd,
      final boolean generateRecursive) {
    Validate.notNull(startDate, "start date");
    Validate.notNull(endDate, "end date");
    Validate.isTrue(periodsPerYear > 0);
    ZonedDateTime[] result = null;
    if (periodsPerYear == 1) {
      if (endOfMonth) {
        if (fromEnd && endDate.getDayOfMonth() == endDate.getMonthOfYear().getLastDayOfMonth(endDate.toLocalDate().isLeapYear())) {
          result = ScheduleCalculatorFactory.ANNUAL_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else if (startDate.getDayOfMonth() == startDate.getMonthOfYear().getLastDayOfMonth(startDate.toLocalDate().isLeapYear())) {
          result = ScheduleCalculatorFactory.ANNUAL_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else {
          result = ScheduleCalculatorFactory.ANNUAL_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        }
      } else {
        result = ScheduleCalculatorFactory.ANNUAL_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
      }
    } else if (periodsPerYear == 2) {
      if (endOfMonth) {
        if (fromEnd && endDate.getDayOfMonth() == endDate.getMonthOfYear().getLastDayOfMonth(endDate.toLocalDate().isLeapYear())) {
          result = ScheduleCalculatorFactory.SEMI_ANNUAL_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else if (startDate.getDayOfMonth() == startDate.getMonthOfYear().getLastDayOfMonth(startDate.toLocalDate().isLeapYear())) {
          result = ScheduleCalculatorFactory.SEMI_ANNUAL_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else {
          result = ScheduleCalculatorFactory.SEMI_ANNUAL_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        }
      } else {
        result = ScheduleCalculatorFactory.SEMI_ANNUAL_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
      }
    } else if (periodsPerYear == 4) {
      if (endOfMonth) {
        if (fromEnd && endDate.getDayOfMonth() == endDate.getMonthOfYear().getLastDayOfMonth(endDate.toLocalDate().isLeapYear())) {
          result = ScheduleCalculatorFactory.QUARTERLY_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else if (startDate.getDayOfMonth() == startDate.getMonthOfYear().getLastDayOfMonth(startDate.toLocalDate().isLeapYear())) {
          result = ScheduleCalculatorFactory.QUARTERLY_EOM_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else {
          result = ScheduleCalculatorFactory.QUARTERLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        }
      } else {
        result = ScheduleCalculatorFactory.QUARTERLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
      }
    } else if (periodsPerYear == 12) {
      if (endOfMonth) {
        if (fromEnd && endDate.getDayOfMonth() == endDate.getMonthOfYear().getLastDayOfMonth(endDate.toLocalDate().isLeapYear())) {
          result = ScheduleCalculatorFactory.END_OF_MONTH_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else if (startDate.getDayOfMonth() == startDate.getMonthOfYear().getLastDayOfMonth(startDate.toLocalDate().isLeapYear())) {
          result = ScheduleCalculatorFactory.END_OF_MONTH_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        } else {
          result = ScheduleCalculatorFactory.MONTHLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
        }
      } else {
        result = ScheduleCalculatorFactory.MONTHLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
      }
    } else if (periodsPerYear == 52) {
      if (endOfMonth) {
        throw new IllegalArgumentException("Cannot get EOM series for weekly frequency");
      }
      result = ScheduleCalculatorFactory.WEEKLY_CALCULATOR.getSchedule(startDate, endDate, fromEnd, generateRecursive);
    } else if (periodsPerYear == 365 || periodsPerYear == 366) {
      if (endOfMonth) {
        throw new IllegalArgumentException("Cannot get EOM series for daily frequency");
      }
      result = ScheduleCalculatorFactory.DAILY_CALCULATOR.getSchedule(startDate, endDate);
    }
    Validate.notNull(result, "result");
    return result;
  }
}
