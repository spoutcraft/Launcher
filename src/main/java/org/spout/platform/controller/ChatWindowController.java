/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.controller;

import com.cathive.fx.guice.FXMLController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

@FXMLController
public class ChatWindowController {
	@FXML
	private TextArea inputField;
	@FXML
	private TextArea chatArea;
	private Chat chat;

	@FXML
	public void initialize() {
		inputField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				String text = inputField.getText();
				if (keyEvent.getCode() == KeyCode.ENTER && text != null && !"".equals(text)) {
					try {
						keyEvent.consume(); // TODO: this doesnt seem to work that well, there is still a new line in the input field afterwards.
						chat.sendMessage(text);
						chatArea.textProperty().set(chatArea.textProperty().get() + "\nME: " + text);
						inputField.setText("");
					} catch (XMPPException e) {
						e.printStackTrace();  //TODO: Logging & Error message.
					}
				}
			}
		});
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				inputField.requestFocus();
			}
		});
	}

	public void populate(Chat chat) {
		this.chat = chat;
		chat.addMessageListener(new MessageListener() {
			@Override
			public void processMessage(Chat chat, Message message) {
				if (message.getType().equals(Message.Type.chat)) {
					chatArea.textProperty().set(chatArea.textProperty().get() + "\n" + chat.getParticipant() + ": " + message.getBody());
				}
			}
		});
	}
}
