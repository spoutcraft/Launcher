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
import javafx.stage.Stage;

import org.spout.platform.chat.manager.ChatManager;
import org.spout.platform.chat.model.Friend;

@FXMLController
public class ChatController {
	@FXML
	private ListView<Friend> friendList;
	@FXML
	private Button addFriend;
	private Stage chatStage;
	@Inject
	private ChatManager chatManager;
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

		friendList.itemsProperty().bind(new SimpleListProperty(chatManager.getFriends()));
		addFriend.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				String username = Dialogs.showInputDialog(chatStage, "Enter Username", "Enter Username");
				String xmppaddress = Dialogs.showInputDialog(chatStage, "Enter XMPP Address", "Enter XMPP Address");
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
		chatManager.connect(xmppServer, xmppPort, "", ""); // <- enter username and password here to test.
	}

	public void setChatStage(Stage chatStage) {
		this.chatStage = chatStage;
	}
}
