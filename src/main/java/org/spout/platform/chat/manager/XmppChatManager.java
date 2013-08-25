/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.chat.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import org.spout.platform.chat.model.Chat;
import org.spout.platform.chat.model.Friend;
import org.spout.platform.controller.ChatWindowController;
import org.spout.platform.gui.Views;

public class XmppChatManager implements ChatManager {
	@Inject
	private GuiceFXMLLoader fxmlLoader;
	private Connection chatConnection = null;
	private ConnectionConfiguration config;
	private ObservableList<Friend> friends = FXCollections.observableArrayList();
	private Map<String, org.jivesoftware.smack.Chat> openChats = new HashMap<>();

	@Override
	public List<Chat> getChats() {
		return null;  // Do we need this?
	}

	@Override
	public ObservableList<Friend> getFriends() {
		return friends;
	}

	@Override
	public void connect(String server, String port, String username, String password) {
		if (chatConnection == null || !chatConnection.isConnected()) {
			config = new ConnectionConfiguration(server, Integer.valueOf(port));
			chatConnection = new XMPPConnection(config);
			try {
				chatConnection.connect();
				chatConnection.login(username, password);
				Collection<RosterEntry> entries = chatConnection.getRoster().getEntries();
				for (RosterEntry entry : entries) {
					Friend friend = new Friend();
					friend.setName(entry.getName());
					friend.setImAddress(entry.getUser());
					friends.add(friend);
				}
				setUpListeners();
			} catch (XMPPException e) {
				//TODO: Error handling
				e.printStackTrace();
			}
		}
	}

	private void setUpListeners() {
		chatConnection.getRoster().addRosterListener(new RosterListener() {
			@Override
			public void entriesAdded(Collection<String> addresses) {
				for (String address : addresses) {
					Friend friend = new Friend();
					RosterEntry entry = chatConnection.getRoster().getEntry(address);
					friend.setName(entry.getName());
					friend.setImAddress(entry.getUser());
					friends.add(friend);
				}
			}

			@Override
			public void entriesUpdated(Collection<String> addresses) {
				for (String address : addresses) {
					Friend foundFriend = null;
					for (Friend friend : friends) {
						if (address.equals(friend.getImAddress())) {
							foundFriend = friend;
							break;
						}
					}
					if (foundFriend == null) {
						return;
					}
					RosterEntry entry = chatConnection.getRoster().getEntry(address);
					foundFriend.setName(entry.getName());
					foundFriend.setImAddress(entry.getUser());
					friends.add(foundFriend);
				}
			}

			@Override
			public void entriesDeleted(Collection<String> addresses) {
				List<Friend> toRemove = new ArrayList<>();
				for (String address : addresses) {
					for (Friend friend : friends) {
						if (friend.getImAddress() != null && friend.getImAddress().equals(address)) {
							toRemove.add(friend);
						}
					}
				}
				friends.removeAll(toRemove);
			}

			@Override
			public void presenceChanged(Presence presence) {
				//TODO: show online status in friendlist
			}
		});
		chatConnection.getChatManager().addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(org.jivesoftware.smack.Chat chat, boolean createdLocally) {
				if (!createdLocally) {
					openChatWindow(chat);
				}
			}
		});
	}

	private void openChatWindow(final org.jivesoftware.smack.Chat chat) {
		final String participant = chat.getParticipant();
		RosterEntry entry = chatConnection.getRoster().getEntry(participant);
		if (openChats.get(entry.getUser()) == null) {
			try {
				GuiceFXMLLoader.Result chatResult = fxmlLoader.load(XmppChatManager.class.getResource(Views.CHAT_WINDOWS_VIEW));
				((ChatWindowController) chatResult.getController()).populate(chat);
				Scene scene = new Scene((Parent) chatResult.getRoot());
				Stage stage = new Stage();
				stage.setTitle(participant);
				stage.setScene(scene);
				openChats.put(participant, chat);
				stage.show();
				// Remove Chat from the map of open chats once the window is closed.
				stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent windowEvent) {
						openChats.remove(participant);
					}
				});
			} catch (IOException e) {
				e.printStackTrace(); //TODO: Error handling
			}
		}
	}

	@Override
	public void disconnect() {
		if (chatConnection != null && chatConnection.isConnected()) {
			chatConnection.disconnect();
			chatConnection = null;
		}
	}

	@Override
	public void addFriend(Friend friend) {
		try {
			chatConnection.getRoster().createEntry(friend.getImAddress(), friend.getName(), new String[] {});
		} catch (XMPPException e) {
			e.printStackTrace();  //TODO: Error handling
		}
	}

	@Override
	public Chat createChat(Friend friend) {
		org.jivesoftware.smack.Chat chat = chatConnection.getChatManager().createChat(friend.getImAddress(), new MessageListener() {
			@Override
			public void processMessage(org.jivesoftware.smack.Chat chat, Message message) {
				// Do nothing in here, we create the actual chat listener in the chat controller.
				// The API requires us to do this.
			}
		});
		openChatWindow(chat);
		return null; // for now.
	}
}
