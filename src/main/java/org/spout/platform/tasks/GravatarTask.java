/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.tasks;

import java.io.IOException;
import java.net.URL;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import org.spout.platform.model.User;
import org.spout.platform.util.GravatarUtils;

public class GravatarTask extends Task<Void> {
	private User user;

	public GravatarTask(User user) {
		this.user = user;
	}

	@Override
	protected Void call() throws Exception {
		URL gravatar = GravatarUtils.getGravatarURL(user.getUsername(), 40, null, false, GravatarUtils.Rating.Good, true);
		try {
			final Image avatarImg;
			avatarImg = new Image(gravatar.openStream());
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					user.avatar().set(avatarImg);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
