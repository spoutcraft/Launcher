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
import javafx.scene.layout.AnchorPane;

import org.spout.platform.gui.grid.GridCell;
import org.spout.platform.gui.grid.GridLayout;

public class DashboardController extends ContentController {
	@FXML
	AnchorPane root;
	GridLayout grid;

	public DashboardController() {
		setName("Dashboard");
	}

	@FXML
	public void initialize() {
		grid = new GridLayout(root);
		grid.addCell(new GridCell());
		grid.addCell(new GridCell());
		GridCell test2 = new GridCell(
				"I'm a bigger cell than the others. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		test2.setWidth(2);
		test2.setHeight(3);
		grid.addCell(test2);

		GridCell test3 = new GridCell(
				"This cell is 1x2. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		test3.setWidth(2);
		test3.setHeight(1);
		grid.addCell(test3);
		grid.addCell(new GridCell());
		grid.addCell(new GridCell());
		grid.addCell(new GridCell());
	}

	@Override
	public Node getRootNode() {
		return root;
	}
}
