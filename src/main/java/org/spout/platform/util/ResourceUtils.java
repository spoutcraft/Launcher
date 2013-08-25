/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {
	public static final String RESOURCE_PATH = "/org/spout/platform/resources/";

	public static InputStream getResourceAsStream(String path) {
		InputStream stream = ResourceUtils.class.getResourceAsStream(RESOURCE_PATH + path);
		if (stream == null) {
			// Eclipse path
			File resource = new File(".\\src\\main\\resources\\" + path);
			if (resource.exists()) {
				try {
					stream = new BufferedInputStream(new FileInputStream(resource));
				} catch (IOException ignore) {
				}
			}
		}
		return stream;
	}
}
