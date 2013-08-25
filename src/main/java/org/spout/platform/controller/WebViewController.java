/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

public class WebViewController extends ContentController {
	@FXML
	private WebView webView;
	@FXML
	private BorderPane webViewPane;

	@Override
	public String getName() {
		return "WebView";
	}

	@Override
	public void onActivate() {
		webView.getEngine().load("http://www.spout.org");
	}

	@Override
	public Node getRootNode() {
		return webViewPane;
	}
}
