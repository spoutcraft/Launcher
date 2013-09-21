package org.spout.platform.gui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import org.spout.platform.chat.model.Friend;

public class FriendListCellFactory implements Callback<ListView<Friend>, ListCell<Friend>> {
	@Override
	public ListCell<Friend> call(ListView<Friend> friendListView) {
		return new FriendListCell();
	}
}
