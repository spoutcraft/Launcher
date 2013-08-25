/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.controller;

import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.beans.property.SimpleListProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialogs;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang.StringUtils;

import org.spout.platform.chat.manager.ChatManager;
import org.spout.platform.chat.model.Friend;
import org.spout.platform.services.PropertyManager;

@FXMLController
public class ChatController {
	private static final String SPOUT_XMPP_USERNAME = "spout.xmpp.username";
	private static final String SPOUT_XMPP_PASSWORD = "spout.xmpp.password";
	@FXML
	private ListView<Friend> friendList;
	@FXML
	private Button addFriend;
	@Inject
	private ChatManager chatManager;
	@Inject
	private PropertyManager propertyManager;
	private String xmppServer;
	private String xmppPort;

	@Inject
	public ChatController(
			@Named ("spout.xmpp.server")
			String xmppServer,
			@Named ("spout.xmpp.port")
			String xmppPort) {
		this.xmppServer = xmppServer;
		this.xmppPort = xmppPort;
	}

	@FXML
	public void initialize() {

		//login:

		String username = propertyManager.getProperty(SPOUT_XMPP_USERNAME);
		String password = propertyManager.getProperty(SPOUT_XMPP_PASSWORD);
		boolean saved = (username != null) && (password != null);

		if (!saved) {

			username = Dialogs.showInputDialog(null, "Enter your username:");
			password = Dialogs.showInputDialog(null, "Enter your password:");
			boolean save = Dialogs.showConfirmDialog(null, "Do you want to save your account details?").equals(Dialogs.DialogResponse.YES);
			if (save) {
				propertyManager.saveProperty(SPOUT_XMPP_USERNAME, username);
				propertyManager.saveProperty(SPOUT_XMPP_PASSWORD, password);
			}
		}

		friendList.itemsProperty().bind(new SimpleListProperty(chatManager.getFriends()));
		addFriend.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				String username = Dialogs.showInputDialog(null, "Enter Username", "Enter Username");
				String xmppaddress = Dialogs.showInputDialog(null, "Enter XMPP Address", "Enter XMPP Address");
				if (StringUtils.isEmpty(username) || StringUtils.isEmpty(xmppaddress)) {
					Dialogs.showErrorDialog(null, "Username and Address may not be empty.", "Empty Address or Username");
				}
				Friend newFriend = new Friend();
				newFriend.setName(username);
				newFriend.setImAddress(xmppaddress);
				chatManager.addFriend(newFriend);
			}
		});
		friendList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (MouseButton.PRIMARY.equals(mouseEvent.getButton()) && mouseEvent.getClickCount() == 2) {
					chatManager.createChat(friendList.getSelectionModel().getSelectedItem());
				}
			}
		});
		chatManager.connect(xmppServer, xmppPort, username, password);
	}
}
