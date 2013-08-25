/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.inject.Inject;

import org.spout.platform.services.PropertyManager;
import org.spout.platform.util.DirectoryUtils;

public class SimplePropertyManager implements PropertyManager {
	private File propertiesFile;
	private Properties properties;
	private Lock lock = new ReentrantLock();

	/**
	 * Constructor. Should never be called directly.
	 */
	@Inject
	public SimplePropertyManager() {
		init();
	}

	private void init() {
		try {
			propertiesFile = new File(DirectoryUtils.getWorkingDirectory(), "launcher.properties");
			if (!propertiesFile.exists()) {
				propertiesFile.getParentFile().mkdirs();
				propertiesFile.createNewFile();
			}
			properties = new Properties();
			properties.load(new FileInputStream(propertiesFile));
		} catch (IOException e) {
			throw new IllegalStateException("Could not open properties file " + propertiesFile.getAbsolutePath());
		}
	}

	@Override
	public void saveProperty(String key, String value) {
		lock.lock();
		try {
			properties.put(key, value);
			properties.store(new FileOutputStream(propertiesFile), "");
		} catch (IOException e) {
			e.printStackTrace();
			//TODO: Error handling.
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
}
