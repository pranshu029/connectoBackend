package com.connectoBackend.common.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utility methods for common validation checks.
 */
public final class ValidationUtil {

	private static final Pattern EMAIL_PATTERN = Pattern.compile(
			"^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
	);

	private static final Pattern PHONE_PATTERN = Pattern.compile(
			"^[+]?[0-9]{7,15}$"
	);

	private static final Pattern USERNAME_PATTERN = Pattern.compile(
			"^[a-zA-Z0-9._]{3,30}$"
	);

	private ValidationUtil() {
	}

	public static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	public static boolean isNotBlank(String value) {
		return !isBlank(value);
	}

	public static boolean isValidEmail(String value) {
		return isNotBlank(value) && EMAIL_PATTERN.matcher(value.trim()).matches();
	}

	public static boolean isValidPhoneNumber(String value) {
		return isNotBlank(value) && PHONE_PATTERN.matcher(value.trim()).matches();
	}

	public static boolean isValidUsername(String value) {
		return isNotBlank(value) && USERNAME_PATTERN.matcher(value.trim()).matches();
	}

	public static boolean isPastDate(LocalDate date) {
		return date != null && date.isBefore(LocalDate.now());
	}

}
