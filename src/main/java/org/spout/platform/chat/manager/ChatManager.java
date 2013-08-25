/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.chat.manager;

import java.util.List;

import javafx.collections.ObservableList;

import org.spout.platform.chat.model.Chat;
import org.spout.platform.chat.model.Friend;

public interface ChatManager {
	List<Chat> getChats();

	ObservableList<Friend> getFriends();

	void connect(String server, String port, String username, String password);

	void disconnect();

	void addFriend(Friend friend);

	Chat createChat(Friend friend);
}
