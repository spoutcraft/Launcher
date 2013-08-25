/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.tasks;

import javafx.concurrent.Task;

import org.spout.platform.model.User;

public class LoginTask extends Task<Void> {
	private User user;
	private String username;
	private String password;

	public LoginTask(User user, String username, String password) {
		this.user = user;
		this.username = username;
		this.password = password;
	}

	@Override
	protected Void call() throws Exception {
		//TODO re-implement once auth has been implemented in hub.
		return null;
	}
}
