/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.gui.grid;

public class ProxyCell extends GridCell {
	private GridCell original;

	public ProxyCell(GridCell original) {
		this.original = original;
		original.proxies.add(this);

		widthProperty().bind(original.widthProperty());
		heightProperty().bind(original.heightProperty());
	}

	public GridCell getOriginal() {
		return original;
	}
}
