/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.gui.grid;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.AnchorPane;

public class GridLayout {
	private AnchorPane root;
	private SimpleIntegerProperty size = new SimpleIntegerProperty(50);
	private SimpleIntegerProperty width = new SimpleIntegerProperty(5);
	private SimpleIntegerProperty height = new SimpleIntegerProperty(5);
	private SimpleIntegerProperty spacing = new SimpleIntegerProperty(15);
	private ArrayList<GridCell> cells = new ArrayList<GridCell>();
	private GridCell[][] layout;

	public GridLayout(AnchorPane root) {
		this.root = root;
		relayout();
		// TODO relayout on width or height change
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

	public int getSize() {
		return size.get();
	}

	public void setSize(int size) {
		this.size.set(size);
	}

	public IntegerProperty sizeProperty() {
		return size;
	}

	public int getSpacing() {
		return spacing.get();
	}

	public void setSpacing(int spacing) {
		this.spacing.set(spacing);
	}

	public IntegerProperty spacingProperty() {
		return spacing;
	}

	public void addCell(GridCell cell) {
		cells.add(cell);
		cell.setGrid(this);
		root.getChildren().add(cell.getRootNode());
		addToNextFree(cell);
	}

	public void relayout() {
		layout = new GridCell[width.get()][];
		for (int x = 0; x < width.get(); x++) {
			layout[x] = new GridCell[height.get()];
		}

		for (GridCell cell : cells) {
			if (!addToNextFree(cell)) {
				System.out.println("No more free cells, expanding vertically!");
				height.add(1); // Since this will relay out, we return.
				return;
			}
		}
	}

	private boolean addToNextFree(GridCell cell) {
		if (cell.getWidth() > getWidth()) {
			return false; // This will never fit.
		}
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (fits(x, y, x + cell.getWidth(), y + cell.getHeight())) {
					cell.setX(x);
					cell.setY(y);
					boolean originalPlaced = false;
					for (int xx = x; xx < x + cell.getWidth(); xx++) {
						for (int yy = y; yy < y + cell.getHeight(); yy++) {
							if (!originalPlaced) {
								layout[xx][yy] = cell;
								originalPlaced = true;
							} else {
								layout[xx][yy] = new ProxyCell(cell);
							}
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	private boolean fits(int x, int y, int width, int height) {
		if (width > getWidth() || height > getHeight()) {
			return false;
		}
		for (int xx = x; xx < width; xx++) {
			for (int yy = y; yy < height; yy++) {
				if (layout[xx][yy] != null) {
					return false;
				}
			}
		}
		return true;
	}
}
