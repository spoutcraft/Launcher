/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.util;

import java.io.File;

public class DirectoryUtils {
	public static File getWorkingDirectory() {
		return getWorkingDirectory("spout");
	}

	public static File getWorkingDirectory(String applicationName) {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;

		OperatingSystem os = OperatingSystem.getOS();
		if (os.isUnix()) {
			workingDirectory = new File(userHome, '.' + applicationName + '/');
		} else if (os.isWindows()) {
			String applicationData = System.getenv("APPDATA");
			if (applicationData != null) {
				workingDirectory = new File(applicationData, "." + applicationName + '/');
			} else {
				workingDirectory = new File(userHome, '.' + applicationName + '/');
			}
		} else if (os.isMac()) {
			workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
		} else {
			workingDirectory = new File(userHome, applicationName + '/');
		}
		if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs())) {
			throw new RuntimeException("The working directory could not be created: " + workingDirectory);
		}
		return workingDirectory;
	}

	public static File getPluginsDirectory() {
		return safeMkDir(new File(getWorkingDirectory(), "plugins"));
	}

	private static File safeMkDir(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
}
