/*
 * MIT License
 *
 * Copyright (c) 2017 Donato Rimenti
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
package co.aurasphere.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Utility class used to prettify JSON. This class is ThreadSafe.
 * 
 * @author Donato Rimenti
 * 
 */
public class JSONPrettifier {

	/**
	 * String used to represent one indentation level.
	 */
	private static final String INDENTATION_CHAR = "  ";

	/**
	 * Formats the JSON into different readable rows. The applied algorithm adds
	 * one or more escape characters to the JSON String following this rules:
	 * 
	 * <pre>
	 * - "{": adds \n, adds a level of indentation
	 * - "}": adds \n, removes a level of indentation
	 * - ",": adds \n
	 * - any other character: adds that character
	 * </pre>
	 * 
	 * This method supports parsing multiple JSON's lines at once and also can
	 * parse truncated JSON as far as it ends with the "...\n" String. Please
	 * notice that the truncation may happen both inside or outside a JSON
	 * field. This means that if you have a field that contains "...\n" you will
	 * have to escape the newline character in order to avoid to truncate the
	 * JSON.
	 * 
	 * The algorithm applied is slow for large inputs since the String is parsed
	 * character by character and thus has a complexity of O(n)(linear).
	 * 
	 * @param data
	 *            the JSON string to prettify.
	 * @return the JSON string prettified.
	 */
	public static String prettify(String data) {
		if (data == null || data == "") {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		// Whether I'm currently parsing a String or not. Used in order to avoid
		// indentation if I'm inside double quotes.
		boolean outsideQuotes = true;
		// Handles the current indentation within the method instead of using
		// an instance field in order to make the class ThreadSafe at the cost
		// of some code cleanliness.
		AtomicInteger currentIndentationLevel = new AtomicInteger();
		char currentChar;
		// Cycles all characters in the JSON String.
		for (int i = 0; i < data.length(); i++) {
			currentChar = data.charAt(i);
			switch (currentChar) {
			// If the char is a "{" then adds a newline and a level of
			// indentation. If i == 0, then we are at the first character of the
			// stream, no need to add a newline at the beginning.
			case '{':
				conditionalIndent(builder, currentIndentationLevel, i == 0, outsideQuotes);
				builder.append(currentChar);
				conditionalIndent(builder, currentIndentationLevel, false, outsideQuotes);
				break;
			// If the char is a "}" then adds a newline and removes a level of
			// indentation.
			case '}':
				conditionalDeindent(builder, currentIndentationLevel, false, outsideQuotes);
				builder.append(currentChar);
				// If the char after this is another "}" or a "," it doesn't had
				// another newline.
				conditionalDeindent(builder, currentIndentationLevel, charAfter(data, i, '}'), outsideQuotes,
						!charAfter(data, i, ','));
				// If this JSON object has been closed, adds an extra newline in
				// order to improve readability.
				conditionalAppend(builder, "\n\n", currentIndentationLevel.get() == 0);
				break;
			// If the char is a "," then removes a level of indentation if the
			// character before was a "}" (because they are sibling objects).
			case ',':
				builder.append(currentChar);
				// Since the two statements in this case checks both on the char
				// before this only one of them is executed.
				boolean wasTokenBeforeAnObject = charBefore(data, i, '}');
				boolean isTokenAfterAnObject = charAfter(data, i, '{');

				// If the char before this was a "}" then before this comma
				// there was an object that has ended and thus removes a level
				// of indentation. It doesn't add a new line if the next
				// character is "{" since it will be added on the next round of
				// parsing.
				conditionalDeindent(builder, currentIndentationLevel, isTokenAfterAnObject, outsideQuotes,
						wasTokenBeforeAnObject);

				// If the char before this one was not a "}" then we didn't
				// parse an object before but just a String or an array. In that
				// case, I don't remove any level of indentation and I append
				// the current indentation level plus a newline if the next
				// character is not a "{" for the same reason as above.
				conditionalAppend(builder, currentIndentationToString(isTokenAfterAnObject, currentIndentationLevel),
						outsideQuotes, !wasTokenBeforeAnObject);
				break;
			// If the char is a "\"" (quote) and it's not escaped, switches the
			// outsideQuotes flag in order to prevent parsing commas and
			// brackets inside quotes as if they were JSON.
			case '"':
				if (!charBefore(data, i, '\\')) {
					outsideQuotes = !outsideQuotes;
				}
				builder.append(currentChar);
				break;
			// If the char is a "\n" (newline) checks if the 3 character before
			// where dots. If that's the case, the JSON has been truncated.
			// Resets the indentation level and other flags and adds a newline
			// to improve readability. Notice that the truncation may happen
			// inside one of the JSON fields as well, so make sure to escape any
			// newline character inside the JSON before parsing it.
			case '\n':
				if (charBefore(data, i, '.') && charBefore(data, i - 1, '.') && charBefore(data, i - 2, '.')) {
					currentIndentationLevel.set(0);
					outsideQuotes = true;
					builder.append("\n\n");
				}
				break;
			// If the char is anything else, just appends it.
			default:
				builder.append(currentChar);
			}
		}

		return builder.toString();
	}

	/**
	 * Returns the current indentation level as a String. The String will be
	 * made of n {@link JSONPrettifier#INDENTATION_CHAR} with n being the
	 * currentIndentation argument passed.
	 * 
	 * @param noNewLine
	 *            whether to omit a new line character ("\n") at the beginning
	 *            of the returned String or not.
	 * @param currentIndentationLevel
	 *            the current indentation level or the number of
	 *            {@link #INDENTATION_CHAR} to append to the returned String.
	 * @return a String representing the current indentation level.
	 */
	private static String currentIndentationToString(boolean noNewLine, AtomicInteger currentIndentationLevel) {
		StringBuilder builder = new StringBuilder();
		int indentation = currentIndentationLevel.get();
		// If the current indentation is 0, doesn't add a newline. This is to
		// prevent the case where there are multiple commas outside the JSON
		// String so that they don't always go on a newline.
		if (indentation > 0 && !noNewLine) {
			builder.append("\n");
		}
		for (int i = 0; i < indentation; i++) {
			builder.append(INDENTATION_CHAR);
		}
		return builder.toString();
	}

	/**
	 * Adds a level of indentation and returns the current indentation level.
	 * 
	 * @param noNewLine
	 *            whether to omit a new line character ("\n") at the beginning
	 *            of the returned String or not.
	 * @param currentIndentationLevel
	 *            the current indentation level or the number of
	 *            {@link #INDENTATION_CHAR} to append to the returned String.
	 * @return a String representing the current indentation level.
	 */
	private static String indent(boolean noNewLine, AtomicInteger currentIndentationLevel) {
		currentIndentationLevel.incrementAndGet();
		return currentIndentationToString(noNewLine, currentIndentationLevel);
	}

	/**
	 * Removes a level of indentation and returns the current indentation level.
	 * 
	 * @param noNewLine
	 *            whether to omit a new line character ("\n") at the beginning
	 *            of the returned String or not.
	 * @param currentIndentationLevel
	 *            the current indentation level or the number of
	 *            {@link #INDENTATION_CHAR} to append to the returned String.
	 * @return a String representing the current indentation level.
	 */
	private static String deindent(boolean noNewLine, AtomicInteger currentIndentationLevel) {
		currentIndentationLevel.decrementAndGet();
		return currentIndentationToString(noNewLine, currentIndentationLevel);
	}

	/**
	 * Checks whether the character before the one at the index passed (ignoring
	 * whitespace characters) is equal to the character passed.
	 * 
	 * @param data
	 *            the String where to perform the check.
	 * @param index
	 *            the index of the character whose preceding character has to be
	 *            checked.
	 * @param character
	 *            the character to match.
	 * @return true if data.charAt(index - 1) == character (ignoring whitespace
	 *         characters), false otherwise
	 */
	private static boolean charBefore(String data, int index, char character) {
		if (index - 1 < 0 || data.length() <= index - 1) {
			return false;
		}
		// Trims whitespace characters.
		do {
			index--;
		} while (index - 1 > 0 && data.charAt(index) == ' ');

		return character == data.charAt(index);
	}

	/**
	 * Checks whether the character after the one at the index passed (ignoring
	 * whitespace characters) is equal to the character passed.
	 * 
	 * @param data
	 *            the String where to perform the check.
	 * @param index
	 *            the index of the character whose subsequent character has to
	 *            be checked.
	 * @param character
	 *            the character to match.
	 * @return true if data.charAt(index + 1) == character (ignoring whitespace
	 *         characters), false otherwise
	 */
	private static boolean charAfter(String data, int index, char character) {
		if (index + 1 < 0 || data.length() <= index + 1) {
			return false;
		}
		// Trims whitespace characters.
		do {
			index++;
		} while (data.length() > index + 1 && data.charAt(index) == ' ');

		return character == data.charAt(index);
	}

	/**
	 * Appends a String to a StringBuilder only if ALL the conditions passed are
	 * met. If no conditions are passed, appends the String.
	 * 
	 * @param builder
	 *            the StringBuilder where to append the String.
	 * @param string
	 *            the String to append.
	 * @param conditions
	 *            the conditions to check before appending the String. If no
	 *            conditions are passed, the String is appended by default.
	 * @return the StringBuilder passed as argument.
	 */
	private static StringBuilder conditionalAppend(StringBuilder builder, String string, boolean... conditions) {
		boolean append = true;
		// Checks all the conditions.
		for (boolean b : conditions) {
			append = append && b;
		}
		if (append) {
			builder.append(string);
		}
		return builder;
	}

	/**
	 * Adds a level of indentation to a StringBuilder only if ALL the conditions
	 * passed are met. If no conditions are passed, adds an indentation level.
	 * 
	 * @param builder
	 *            the StringBuilder where to append the indentation.
	 * @param noNewLine
	 *            whether to omit a new line character ("\n") at the beginning
	 *            of the appended indentation.
	 * @param currentIndentationLevel
	 *            the current indentation level or the number of
	 *            {@link #INDENTATION_CHAR} to append when indenting.
	 * @param conditions
	 *            the conditions to check before adding the indentation. If no
	 *            conditions are passed, the indentation is added by default.
	 * @return the StringBuilder passed as argument.
	 */
	private static StringBuilder conditionalIndent(StringBuilder builder, AtomicInteger currentIndentationLevel,
			boolean noNewLine, boolean... conditions) {
		boolean append = true;
		// Checks all the conditions.
		for (boolean b : conditions) {
			append = append && b;
		}

		if (append) {
			builder.append(indent(noNewLine, currentIndentationLevel));
		}
		return builder;
	}

	/**
	 * Removes a level of indentation to a StringBuilder only if ALL the
	 * conditions passed are met. If no conditions are passed, removes an
	 * indentation level.
	 * 
	 * Please notice that this method removes a level of indentation by actually
	 * appending a new indentation that is one level below the one passed as
	 * argument.
	 * 
	 * @param builder
	 *            the StringBuilder where to remove the indentation.
	 * @param noNewLine
	 *            whether to omit a new line character ("\n") at the beginning
	 *            of the appended indentation.
	 * @param currentIndentationLevel
	 *            the current indentation level or the number of
	 *            {@link #INDENTATION_CHAR} to append when indenting.
	 * @param conditions
	 *            the conditions to check before removing. If no conditions are
	 *            passed, the indentation is removed by default.
	 * @return the StringBuilder passed as argument.
	 */
	private static StringBuilder conditionalDeindent(StringBuilder builder, AtomicInteger currentIndentationLevel,
			boolean noNewLine, boolean... conditions) {
		boolean append = true;
		// Checks all the conditions.
		for (boolean b : conditions) {
			append = append && b;
		}

		if (append) {
			builder.append(deindent(noNewLine, currentIndentationLevel));
		}
		return builder;
	}

}
