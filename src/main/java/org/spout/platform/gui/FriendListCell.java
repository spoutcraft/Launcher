/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.gui;

import javafx.scene.control.ListCell;

import org.spout.platform.chat.model.Friend;
import org.spout.platform.gui.font.FontAwesome;

public class FriendListCell extends ListCell<Friend> {
	@Override
	protected void updateItem(Friend friend, boolean empty) {
		super.updateItem(friend, empty);
		if (empty || friend == null) {
			super.setGraphic(null);
			super.setText(null);
		} else {
			super.setText(friend.getName());
			switch (friend.getStatus()) {
				case AVAILABLE:
					super.setGraphic(FontAwesome.createIconLabel(FontAwesome.ICON_COMMENT_ALT));
					break;
				case AWAY:
					super.setGraphic(FontAwesome.createIconLabel(FontAwesome.ICON_MINUS_SIGN));
					break;
				case DND:
					super.setGraphic(FontAwesome.createIconLabel(FontAwesome.ICON_EXCLAMATION_SIGN));
					break;
				case CHAT:
					super.setGraphic(FontAwesome.createIconLabel(FontAwesome.ICON_COMMENTS_ALT));
					break;
				case NA:
					super.setGraphic(FontAwesome.createIconLabel(FontAwesome.ICON_REMOVE_SIGN));
					break;
				case OFFLINE:
					super.setGraphic(FontAwesome.createIconLabel(FontAwesome.ICON_COMMENT));
					break;
			}
		}
	}
}
