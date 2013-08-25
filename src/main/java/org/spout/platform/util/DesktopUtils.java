/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Utility class for easy access to Desktop - related functionality.
 */
public final class DesktopUtils {
	private DesktopUtils() {
		// Utility Class
	}

	/**
	 * Opens an URL in the users browser.
	 *
	 * @param url the URL to open.
	 */
	public static void openUrl(String url) {
		// OS X and the Desktop class don't get along to well. Have this ugly workaround!
		if (!OperatingSystem.MAC_OSX.equals(OperatingSystem.getOS())) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(URI.create(url));
				} catch (final IOException e) {
					// TODO: Logging
					e.printStackTrace();
				}
			} else {
				// TODO: Logging
				System.out.println("Desktop not supported");
			}
		} else {
			try {
				final Process process = Runtime.getRuntime().exec("open " + url);
				process.getInputStream().close();
				process.getErrorStream().close();
			} catch (final IOException e) {
				// TODO: Logging
				e.printStackTrace();
			}
		}
	}
}
