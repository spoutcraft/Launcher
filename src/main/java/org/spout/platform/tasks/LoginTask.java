/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.tasks;

import java.net.Socket;

import javafx.application.Platform;
import javafx.concurrent.Task;

import org.spout.auth.shared.crypto.CryptoHelper;
import org.spout.auth.shared.crypto.RSACrypto;
import org.spout.auth.shared.network.Network;
import org.spout.auth.shared.packets.Packet;
import org.spout.auth.shared.packets.Packet0Kill;
import org.spout.auth.shared.packets.Packet1CryptoSetup;
import org.spout.auth.shared.packets.Packet2AuthRequest;
import org.spout.auth.shared.packets.Packet3AuthResponse;
import org.spout.auth.shared.packets.Packet8MinecraftLink;
import org.spout.platform.model.User;

public class LoginTask extends Task<Void> {
	private User user;
	private byte[] sessionKey;
	private RSACrypto crypto = new RSACrypto();
	private String username;
	private String password;

	public LoginTask(User user, String username, String password) {
		this.user = user;
		this.username = username;
		this.password = password;
	}

	@Override
	protected Void call() throws Exception {
		Socket socket = null;
		try {
			sessionKey = CryptoHelper.generateRandomBlock();
			crypto.setPublicKey(RSACrypto.spoutPublicKey);

			socket = new Socket("auth.spout.org", 5350);
			Packet1CryptoSetup packet1 = new Packet1CryptoSetup(crypto.encrypt(sessionKey));
			Network.sendPacket(socket, packet1, null);

			Packet2AuthRequest packet2 = new Packet2AuthRequest(username, password);
			Network.sendPacket(socket, packet2, sessionKey);

			while (true) {
				Packet packet = Network.listenForPacket(socket, sessionKey);

				if (packet instanceof Packet3AuthResponse) {
					Packet3AuthResponse pks = (Packet3AuthResponse) packet;
					//System.out.println("Login success: '" + pks.isSuccess() + "'");

					if (!pks.isSuccess()) {
						throw new Exception("Email or password incorrect");
					} else {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								user.usernameProperty().set(username);
							}
						});
					}

					for (byte[] nonce : pks.getNonces()) {
						user.addNonce(nonce);
					}

					//Packet6RequestInfoPacket packet6 = new
					//Packet6RequestInfoPacket(Packets.Packet8MinecraftLink);
					//Network.sendPacket(socket, packet6, sessionKey);
					break;
				} else if (packet instanceof Packet8MinecraftLink) {
					//Packet8MinecraftLink pks2 = (Packet8MinecraftLink) packet;
					//System.out.println(pks2.isLinked());
					//System.out.println(pks2.getInGameName());

					break;
				}
			}
		} catch (Exception e) {
			throw (e);
		} finally {
			try {
				Packet0Kill packet0 = new Packet0Kill();
				Network.sendPacket(socket, packet0, sessionKey);
				socket.close();
			} catch (Exception e) {
			}
			;
		}
		return null;
	}
}
