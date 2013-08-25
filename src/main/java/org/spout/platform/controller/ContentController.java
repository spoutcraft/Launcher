/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Base class for all controllers that live in the content area of the application.
 */
public abstract class ContentController {
	private StringProperty name = new SimpleStringProperty("<name>");
	protected Button navigationButton = null;
	protected ApplicationController applicationController = null;

	public void onActivate() {
		// Callback - design for extension
	}

	public void onDeactivate() {
		// Callback - design for extension
	}

	public void onQuit() {
		// Callback - design for extension
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty nameProperty() {
		return name;
	}

	public abstract Node getRootNode();

	public final void setApplicationController(ApplicationController applicationController) {
		if (this.applicationController != null) {
			throw new IllegalStateException("application controller was already set");
		}
		this.applicationController = applicationController;
	}

	public final void setNavigationButton(Button activationButton) {
		if (this.navigationButton != null) {
			throw new IllegalStateException("navigation button was already set");
		}
		this.navigationButton = activationButton;
	}

	public Button getNavigationButton() {
		return navigationButton;
	}
}
