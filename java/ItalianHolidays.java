/*
 * MIT License
 *
 * Copyright (c) 2018 Donato Rimenti
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package co.aurasphere.scripts;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility class for handling italian holidays. This class is thread safe. <br>
 * <br>
 * Here's a table with all official holidays for reference:
 * 
 * <table>
 * <tbody>
 * <tr>
 * <th>Date</th>
 * <th>English Name</th>
 * <th>Local Name</th>
 * </tr>
 * <tr>
 * <td>1 January</td>
 * <td>New Year's Day</td>
 * <td><i>Capodanno</i></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>6 January</td>
 * <td>Epiphany</td>
 * <td><i>Epifania</i></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td><i>Variable</i></td>
 * <td>Easter</td>
 * <td><i>Pasqua</i></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td><i>Monday after Easter</i></td>
 * <td>Easter Monday</td>
 * <td><i>Lunedì dell'Angelo</i>, <i>Lunedì in Albis</i> or more commonly
 * <i>Pasquetta</i></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>25 April</td>
 * <td>Liberation Day</td>
 * <td><i>Festa della Liberazione</i></td>
 * </tr>
 * <tr>
 * <td>1 May</td>
 * <td>International Workers' Day</a></td>
 * <td><i>Festa del Lavoro</i> (or <i>Festa dei Lavoratori</i>)</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>2 June</td>
 * <td>Republic Day</td>
 * <td><i>Festa della Repubblica</i></td>
 * </tr>
 * <tr>
 * <td>15 August</td>
 * <td>Ferragosto/Assumption Day</td>
 * <td><i>Ferragosto</i> or <i>Assunzione</i></td>
 * </tr>
 * <tr>
 * <td>1 November</td>
 * <td>All Saints' Day</td>
 * <td><i>Tutti i santi</i> (or <i>Ognissanti</i>)</td>
 * </tr>
 * <tr>
 * <td>8 December</td>
 * <td>Immaculate Conception</td>
 * <td><i>Immacolata Concezione</i> (or just <i>Immacolata</i>)</td>
 * </tr>
 * <tr>
 * <td>25 December</td>
 * <td>Christmas Day</td>
 * <td><i>Natale</i></td>
 * </tr>
 * <tr>
 * <td>26 December</td>
 * <td>St. Stephen's Day</td>
 * <td><i>Santo Stefano</i></td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @author Donato Rimenti
 *
 */
public class ItalianHolydays {

	/**
	 * Formatter for the italian format "dd/MM/yyyy" (UNI EN 28601). <br>
	 * <br>
	 * <b>Access to this object must be synchronized externally since it's not
	 * thread safe.</b> For this reason, it's not exposed by the public API. Use
	 * the thread safe {@link #formatDateToItalianFormat(Calendar)} method
	 * instead.
	 */
	private final SimpleDateFormat ITALIAN_DATE_FORMAT = new SimpleDateFormat(
			"dd/MM/yyyy");

	/**
	 * Formatter for the italian short format "dd/MM/yy". <br>
	 * <br>
	 * <b>Access to this object must be synchronized externally since it's not
	 * thread safe.</b> For this reason, it's not exposed by the public API. Use
	 * the thread safe {@link #formatDateToItalianShortFormat(Calendar)} method
	 * instead.
	 */
	private final SimpleDateFormat ITALIAN_SHORT_DATE_FORMAT = new SimpleDateFormat(
			"dd/MM/yy");

	/**
	 * Fixed italian holidays in a year. This array contains all holidays except
	 * Easter (Pasqua) and Easter Monday (Pasquetta) which are computed for each
	 * year. <br>
	 * <br>
	 * Here's a full list of holidays in this array:
	 * 
	 * <table>
	 * <tbody>
	 * <tr>
	 * <th>Date</th>
	 * <th>English Name</th>
	 * <th>Local Name</th>
	 * </tr>
	 * <tr>
	 * <td>1 January</td>
	 * <td>New Year's Day</td>
	 * <td><i>Capodanno</i></td>
	 * <td></td>
	 * </tr>
	 * <tr>
	 * <td>6 January</td>
	 * <td>Epiphany</td>
	 * <td><i>Epifania</i></td>
	 * <td></td>
	 * </tr>
	 * <tr>
	 * <td>25 April</td>
	 * <td>Liberation Day</td>
	 * <td><i>Festa della Liberazione</i></td>
	 * </tr>
	 * <tr>
	 * <td>1 May</td>
	 * <td>International Workers' Day</a></td>
	 * <td><i>Festa del Lavoro</i> (or <i>Festa dei Lavoratori</i>)</td>
	 * <td></td>
	 * </tr>
	 * <tr>
	 * <td>2 June</td>
	 * <td>Republic Day</td>
	 * <td><i>Festa della Repubblica</i></td>
	 * </tr>
	 * <tr>
	 * <td>15 August</td>
	 * <td>Ferragosto/Assumption Day</td>
	 * <td><i>Ferragosto</i> or <i>Assunzione</i></td>
	 * </tr>
	 * <tr>
	 * <td>1 November</td>
	 * <td>All Saints' Day</td>
	 * <td><i>Tutti i santi</i> (or <i>Ognissanti</i>)</td>
	 * </tr>
	 * <tr>
	 * <td>8 December</td>
	 * <td>Immaculate Conception</td>
	 * <td><i>Immacolata Concezione</i> (or just <i>Immacolata</i>)</td>
	 * </tr>
	 * <tr>
	 * <td>25 December</td>
	 * <td>Christmas Day</td>
	 * <td><i>Natale</i></td>
	 * </tr>
	 * <tr>
	 * <td>26 December</td>
	 * <td>St. Stephen's Day</td>
	 * <td><i>Santo Stefano</i></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	public final Calendar[] fixedHolidays = { fromDate(1, Calendar.JANUARY),
			fromDate(6, Calendar.JANUARY), fromDate(25, Calendar.APRIL),
			fromDate(1, Calendar.MAY), fromDate(2, Calendar.JUNE),
			fromDate(15, Calendar.AUGUST), fromDate(1, Calendar.NOVEMBER),
			fromDate(8, Calendar.DECEMBER), fromDate(25, Calendar.DECEMBER),
			fromDate(26, Calendar.DECEMBER) };

	/**
	 * Holder for an instance of this object, used for the
	 * initialization-on-demand holder singleton idiom.
	 * 
	 * @author Donato Rimenti
	 *
	 */
	private static class InstanceHolder {

		/**
		 * An instance of this object.
		 */
		private static final ItalianHolydays INSTANCE = new ItalianHolydays();
	}

	/**
	 * Private constructor for utility class.
	 */
	private ItalianHolydays() {
	}

	/**
	 * Returns a new or an existing instance of this {@link ItalianHolydays}
	 * object. This method is thread safe.
	 * 
	 * @return an existing or a new instance of this object
	 */
	public static ItalianHolydays getInstance() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * Returns a calendar object for a specified day and month of this year.
	 * 
	 * @param day
	 *            the day of the calendar
	 * @param month
	 *            the month of the calendar
	 * @return a calendar object with this year and the day and month passed as
	 *         argument
	 */
	public Calendar fromDate(int day, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar;
	}

	/**
	 * Checks if a given date is an holiday.
	 * 
	 * @param date
	 *            the date to check
	 * @return true if the date passed as argument is an holiday, false
	 *         otherwise
	 */
	public boolean isHoliday(Calendar date) {
		// Check if it's a fixed holiday.
		for (Calendar holiday : fixedHolidays) {
			if (sameDayAndMonth(date, holiday)) {
				return true;
			}
		}

		// Check if it's Easter of monday after Easter.
		Calendar easter = getEasterForYear(date.get(Calendar.YEAR));
		Calendar mondayAfterEaster = getMondayAfterEasterFromEaster(easter);
		if (sameDayAndMonth(date, easter)
				|| sameDayAndMonth(date, mondayAfterEaster)) {
			return true;
		}

		// Not an holiday.
		return false;
	}

	/**
	 * Checks if a given date is a weekend day.
	 * 
	 * @param date
	 *            the date to check
	 * @return true if the given date is a weekend day, false otherwise
	 */
	public boolean isWeekend(Calendar date) {
		return date.get(Calendar.DAY_OF_WEEK) > 5;
	}

	/**
	 * Checks if a given date is a weekend day or an holiday.
	 * 
	 * @param date
	 *            the date to check
	 * @return true if the given date is a weekend day or an holiday, false
	 *         otherwise
	 */
	public boolean isWeekendOrHoliday(Calendar date) {
		return isWeekend(date) || isHoliday(date);
	}

	/**
	 * Checks if a given date is a working day.
	 * 
	 * @param date
	 *            the date to check
	 * @return true if the given date is a working day, false if it's a weekend
	 *         or holiday
	 */
	public boolean isWorkingDay(Calendar date) {
		return !isWeekendOrHoliday(date);
	}

	/**
	 * Computes the Easter date for the year passed as argument by using the
	 * Gauss algorithm.
	 * 
	 * @param year
	 *            the year whose Easter needs to be computed
	 * @return the Easter date for the year passed as argument
	 */
	public Calendar getEasterForYear(int year) {
		int a = year % 19;
		int b = (int) (year / 100);
		int c = year % 100;
		int d = (int) (b / 4);
		int e = b % 4;
		int f = (int) ((b + 8) / 25);
		int g = (int) ((b - f + 1) / 3);
		int h = (19 * a + b - d - g + 15) % 30;
		int i = (int) (c / 4);
		int k = c % 4;
		int l = (32 + 2 * e + 2 * i - h - k) % 7;
		int m = (int) ((a + 11 * h - 221) / 451);
		int n = (int) (h + l - 7 * m + 114) / 31;
		int p = (h + l - 7 * m + 114) % 31;

		// Actual day and month.
		int day = p + 1;
		int month = n;

		// Returns a calendar.
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day);
		return calendar;
	}

	/**
	 * Returns the date of the monday after Easter, computed by getting the
	 * Easter date through {@link #getEasterForYear(int)} and adding one day.
	 * 
	 * @param year
	 *            the year whose monday after Easter needs to be computed
	 * @return the date of monday after Easter for the year passed as argument
	 */
	public Calendar getMondayAfterEasterForYear(int year) {
		Calendar mondayAfterEaster = getEasterForYear(year);
		mondayAfterEaster.add(Calendar.DAY_OF_MONTH, 1);
		return mondayAfterEaster;
	}

	/**
	 * Utility method for returning the monday after Easter from an Easter date.
	 * The date is computed by adding one day to the date passed as argument. <br>
	 * <br>
	 * <b>N.B. : the calendar passed as argument to this method is not modified
	 * but cloned instead</b>
	 * 
	 * @param easter
	 *            the Easter date. This calendar is not modified but cloned
	 *            instead.
	 * @return the monday after the Easter passed as argument
	 */
	public Calendar getMondayAfterEasterFromEaster(Calendar easter) {
		Calendar mondayAfterEaster = Calendar.getInstance();
		mondayAfterEaster.setTime(easter.getTime());
		mondayAfterEaster.add(Calendar.DAY_OF_MONTH, 1);
		return mondayAfterEaster;
	}

	/**
	 * Alias for {@link #getMondayAfterEasterForYear(int)}.
	 * 
	 * @param year
	 *            the year whose Pasquetta needs to be computed
	 * @return the date of Pasquetta for the year passed as argument
	 */
	public Calendar getPasquettaForYear(int year) {
		return getMondayAfterEasterForYear(year);
	}

	/**
	 * Alias for {@link #getMondayAfterEasterFromEaster(Calendar)}.
	 * 
	 * @param pasqua
	 *            the Pasqua date. This calendar is not modified but cloned
	 *            instead.
	 * @return the Pasquetta after the Pasqua passed as argument
	 */
	public Calendar getPasquettaFromPasqua(Calendar pasqua) {
		return getMondayAfterEasterFromEaster(pasqua);
	}

	/**
	 * Checks if two calendars refers to the same day and month.
	 * 
	 * @param firstDate
	 *            the first date to check
	 * @param secondDate
	 *            the second date to check
	 * @return true if the calendars passed as arguments have the same day and
	 *         month, false otherwise
	 */
	public boolean sameDayAndMonth(Calendar firstDate, Calendar secondDate) {
		return firstDate.get(Calendar.MONTH) == secondDate.get(Calendar.MONTH)
				&& firstDate.get(Calendar.DAY_OF_MONTH) == secondDate
						.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Adds the working days passed as arguments to the given calendar. For
	 * subtraction, you can either pass a negative number or call the more
	 * straightforward {@link #subtractWorkingDays(Calendar, int)} method.
	 * 
	 * <b>Note that the calendar passed as argument to this method is actually
	 * modified</b>.
	 * 
	 * @param date
	 *            the date whose days needs to be incremented
	 * @param workingDays
	 *            the working days to add to the given date
	 * @return the same calendar object with the working days passed as argument
	 *         added
	 */
	public Calendar addWorkingDays(Calendar date, int workingDays) {
		int counter = 0;
		while (counter < workingDays) {
			date.add(Calendar.DAY_OF_MONTH, 1);

			// A day is considered added only if it's a working day.
			if (isWorkingDay(date)) {
				counter++;
			}
		}
		return date;
	}

	/**
	 * Subtracts the working days passed as arguments from the given calendar.
	 * For addiction, you can either pass a negative number or call the more
	 * straightforward {@link #addWorkingDays(Calendar, int)} method.
	 * 
	 * <b>Note that the calendar passed as argument to this method is actually
	 * modified</b>.
	 * 
	 * @param date
	 *            the date whose days needs to be decremented
	 * @param workingDays
	 *            the working days to subtract from the given date
	 * @return the same calendar object with the working days passed as argument
	 *         subtracted
	 */
	public Calendar subtractWorkingDays(Calendar date, int workingDays) {
		return addWorkingDays(date, -workingDays);
	}

	/**
	 * Formats a date in the italian format "dd/MM/yyyy" (UNI EN 28601). This
	 * method is thread safe and guarded by the lock on
	 * {@link #ITALIAN_DATE_FORMAT}.
	 * 
	 * @param date
	 *            the date to format
	 * @return a date formatted as a "dd/MM/yyyy" string
	 */
	public String formatDateToItalianFormat(Calendar date) {
		// No need to acquire the whole object lock. This also prevents
		// client code degrading the performance if for some reasons a lock on
		// the object instance is acquired.
		synchronized (ITALIAN_DATE_FORMAT) {
			return ITALIAN_DATE_FORMAT.format(date);
		}
	}

	/**
	 * Formats a date in the italian format "dd/MM/yy". This method is thread
	 * safe and guarded by the lock on {@link #ITALIAN_SHORT_DATE_FORMAT}.
	 * 
	 * @param date
	 *            the date to format
	 * @return a date formatted as a "dd/MM/yy" string
	 */
	public String formatDateToItalianShortFormat(Calendar date) {
		// No need to acquire the whole object lock. This also prevents
		// client code degrading the performance if for some reasons a lock on
		// the object instance is acquired.
		synchronized (ITALIAN_SHORT_DATE_FORMAT) {
			return ITALIAN_SHORT_DATE_FORMAT.format(date);
		}
	}

}
