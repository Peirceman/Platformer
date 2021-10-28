package com.platformer.util;

import java.util.Arrays;

public class Format {
	public static String formatInt(String number) {
		if (!number.matches("\\d+")) throw new IllegalArgumentException("must be valid integer, without commas");
		StringBuilder newNumber = new StringBuilder();

		for (int i = number.length(); i >= 0;) {
			for (int j = 0; j < 3; j++) {
				if (--i < 0) break;

				newNumber.append(number.charAt(i));
			}

			if (i > 0) newNumber.append(".");
		}

		return newNumber.reverse().toString();
	}

	public static String formatInt(int number) {
		return formatInt(Integer.toString(number));
	}

	public static <T> String withoutBraces(T[] ar) {
		return Arrays.toString(ar).substring(1).replaceAll("]$", "");
	}
}
