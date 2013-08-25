/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class User {
	private SimpleStringProperty username = new SimpleStringProperty(null);
	private SimpleBooleanProperty loggedIn = new SimpleBooleanProperty(false);
	private SimpleObjectProperty<Image> avatar = new SimpleObjectProperty<Image>(null);
	private Set<byte[]> nonces = new HashSet<byte[]>();

	public String getUsername() {
		return username.get();
	}

	public boolean isLoggedIn() {
		return loggedIn.get();
	}

	public StringProperty usernameProperty() {
		return username;
	}

	public BooleanProperty loggedInProperty() {
		return loggedIn;
	}

	public SimpleObjectProperty<Image> avatar() {
		return avatar;
	}

	public byte[] getAndDiscardNonce() {
		Iterator<byte[]> iter = nonces.iterator();
		byte[] ret = null;
		if (iter.hasNext()) {
			ret = iter.next();
			iter.remove();
		}
		if (nonces.size() == 1) {
			// TODO: Get new nonces
		}
		return ret;
	}

	public void addNonce(byte[] nonce) {
		nonces.add(nonce);
	}
}
