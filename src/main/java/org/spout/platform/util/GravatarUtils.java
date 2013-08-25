/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.util;

import java.net.MalformedURLException;
import java.net.URL;

public class GravatarUtils {
	private static final String GRAVATAR_BASE = "http://www.gravatar.com/avatar/";
	private static final String GRAVATAR_SECURE = "https://secure.gravatar.com/avatar/";

	public static String getEmailHash(String email) {
		String ret = MD5Utils.getMD5(email.trim().toLowerCase());
		return ret;
	}

	public static URL getGravatarURL(String email, int size, String def, boolean forceDefault, Rating rating, boolean secure) {
		String hash = getEmailHash(email);
		StringBuilder url = new StringBuilder();
		url.append(secure ? GRAVATAR_SECURE : GRAVATAR_BASE)
				.append(hash).append(".jpg");
		if (size > 0) {
			url.append("s=").append(size);
		}
		if (def != null) {
			url.append("d=").append(def);
		}
		if (forceDefault) {
			url.append("f=y");
		}
		if (rating != null) {
			url.append("r=").append(rating.getCode());
		}
		try {
			return new URL(url.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public enum Rating {
		Good("g"),
		/**
		 * Suitable for display on all websites with any audience type.
		 */
		ParentalGuidance("pg"),
		/**
		 * May contain rude gestures, provocatively dressed individuals, the lesser swear words, or mild violence.
		 */
		Rated("r"),
		/**
		 * May contain such things as harsh profanity, intense violence, nudity, or hard drug use.
		 */
		Extreme("x"),
		/**
		 * May contain hardcore sexual imagery or extremely disturbing violence.
		 */
		;
		final String code;

		Rating(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}
}
