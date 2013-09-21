/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.chat.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class Friend {
	private StringProperty name = new SimpleStringProperty("");
	private StringProperty imAddress = new SimpleStringProperty("");
	private ObjectProperty<Image> avatar = new SimpleObjectProperty<>();
	private ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.OFFLINE);

	public Status getStatus() {
		return status.get();
	}

	public ObjectProperty<Status> statusProperty() {
		return status;
	}

	public void setStatus(Status status) {
		this.status.set(status);
	}

	public Image getAvatar() {
		return avatar.get();
	}

	public ObjectProperty<Image> avatarProperty() {
		return avatar;
	}

	public void setAvatar(Image avatar) {
		this.avatar.set(avatar);
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getImAddress() {
		return imAddress.get();
	}

	public StringProperty imAddressProperty() {
		return imAddress;
	}

	public void setImAddress(String imAddress) {
		this.imAddress.set(imAddress);
	}

	@Override
	public String toString() {
		return getImAddress();
	}

	public static enum Status {
		AVAILABLE, OFFLINE, AWAY, NA, CHAT, DND
	}
}