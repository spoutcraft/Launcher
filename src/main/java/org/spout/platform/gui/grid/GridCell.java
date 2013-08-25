/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.gui.grid;

import java.util.LinkedList;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class GridCell {
	private SimpleIntegerProperty x = new SimpleIntegerProperty();
	private SimpleIntegerProperty y = new SimpleIntegerProperty();
	private SimpleIntegerProperty width = new SimpleIntegerProperty(1);
	private SimpleIntegerProperty height = new SimpleIntegerProperty(1);
	private Node rootNode;
	private GridLayout grid;
	private NumberBinding screenX;
	private NumberBinding screenY;
	private NumberBinding screenWidth;
	private NumberBinding screenHeight;
	protected LinkedList<ProxyCell> proxies = new LinkedList<ProxyCell>();

	public GridCell() {
		this("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
	}

	public GridCell(String text) {
		rootNode = new Label(text);
		((Label) rootNode).setWrapText(true);
	}

	public void setWidth(int width) {
		this.width.set(width);
	}

	public void setHeight(int height) {
		this.height.set(height);
	}

	public int getWidth() {
		return this.width.get();
	}

	public int getHeight() {
		return this.height.get();
	}

	public IntegerProperty widthProperty() {
		return width;
	}

	public IntegerProperty heightProperty() {
		return height;
	}

	public void setRootNode(Node rootPane) {
		this.rootNode = rootPane;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public int getX() {
		return x.get();
	}

	public void setX(int x) {
		this.x.set(x);
	}

	public SimpleIntegerProperty xProperty() {
		return x;
	}

	public int getY() {
		return y.get();
	}

	public void setY(int y) {
		this.y.set(y);
	}

	public SimpleIntegerProperty yProperty() {
		return y;
	}

	public void setGrid(GridLayout grid) {
		this.grid = grid;
		screenWidth = Bindings.add(Bindings.multiply(widthProperty().subtract(1), grid.spacingProperty()), Bindings.multiply(widthProperty(), grid.sizeProperty()));
		screenHeight = Bindings.add(Bindings.multiply(heightProperty().subtract(1), grid.spacingProperty()), Bindings.multiply(heightProperty(), grid.sizeProperty()));
		screenX = Bindings.multiply(xProperty(), grid.sizeProperty()).add(xProperty().multiply(grid.spacingProperty()));
		screenY = Bindings.multiply(yProperty(), grid.sizeProperty()).add(yProperty().multiply(grid.spacingProperty()));
		screenX.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				AnchorPane.setLeftAnchor(getRootNode(), (double) (Integer) arg2);
			}
		});

		screenY.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				AnchorPane.setTopAnchor(getRootNode(), (double) (Integer) arg2);
			}
		});

		if (getRootNode() instanceof Control) {
			((Control) getRootNode()).prefWidthProperty().bind(screenWidth);
			((Control) getRootNode()).prefHeightProperty().bind(screenHeight);
		} else if (getRootNode() instanceof Pane) {
			((Pane) getRootNode()).prefWidthProperty().bind(screenWidth);
			((Pane) getRootNode()).prefHeightProperty().bind(screenHeight);
		}
	}

	public GridLayout getGrid() {
		return grid;
	}
}
