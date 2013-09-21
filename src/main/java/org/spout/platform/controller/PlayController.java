/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.cathive.fx.guice.FXMLController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.io.FileUtils;

import org.spout.platform.util.DirectoryUtils;

@FXMLController
public class PlayController extends ContentController {
	@FXML
	private ProgressIndicator progress;
	@FXML
	private Button playButton;
	@FXML
	private AnchorPane root;

	public PlayController() {
		setName("Play!");
	}

	@FXML
	public void initialize() {
		progress.setVisible(false);
	}

	@Override
	public Node getRootNode() {
		return root;
	}

	@FXML
	public void onPlayClicked(ActionEvent actionEvent) throws IOException {
		progress.setVisible(true);

		Task<Void> runTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				playButton.setDisable(true);
				File clientHome = new File(DirectoryUtils.getWorkingDirectory(), "spout-client");
				clientHome.mkdirs();
				File serverHome = new File(DirectoryUtils.getWorkingDirectory(), "spout-server");
				serverHome.mkdirs();
				File clientPlugins = new File(clientHome, "plugins");
				clientPlugins.mkdirs();
				File serverPlugins = new File(serverHome, "plugins");
				serverPlugins.mkdirs();
				File spoutClient = new File(clientHome, "spout.jar");
				FileUtils.copyURLToFile(new URL("http://nexus.spout.org/service/local/artifact/maven/redirect?r=snapshots&g=org.spout&a=spout&v=1.0.0-SNAPSHOT"), spoutClient);
				File vanillaClient = new File(clientPlugins, "vanilla.jar");
				FileUtils.copyURLToFile(new URL("http://nexus.spout.org/service/local/artifact/maven/redirect?r=snapshots&g=org.spout&a=vanilla&v=1.6.2-SNAPSHOT"), vanillaClient);
				File spoutServer = new File(serverHome, "spout.jar");
				FileUtils.copyFile(spoutClient, spoutServer);
				FileUtils.copyFile(vanillaClient, new File(serverPlugins, "vanilla.jar"));

				ProcessBuilder processBuilder = new ProcessBuilder();
				processBuilder.directory(serverHome);
				processBuilder.command("java", "-jar", spoutServer.getAbsolutePath(), "-d", "-p", "server");
				launchGameThread(processBuilder, "SERVER");
				processBuilder = new ProcessBuilder();
				processBuilder.directory(clientHome);
				processBuilder.command("java", "-jar", spoutClient.getAbsolutePath(), "-d", "-p", "client");
				launchGameThread(processBuilder, "CLIENT");
				return null;
			}
		};
		runTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						progress.setVisible(false);
						playButton.setDisable(false);
					}
				});
			}
		});
		runTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				Throwable exception = workerStateEvent.getSource().getException();
				if (exception != null) {
					exception.printStackTrace();
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						progress.setVisible(false);
						playButton.setDisable(false);
					}
				});
			}
		});
		playButton.setDisable(true);
		applicationController.getExecutorService().submit(runTask);
	}

	private void launchGameThread(final ProcessBuilder processBuilder, final String name) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					processBuilder.redirectErrorStream(true);
					Process process = processBuilder.start();
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println("[" + name + "] " + line);
					}
					process.waitFor();
				} catch (IOException | InterruptedException e) {
					System.out.println(processBuilder.command().toString());
					e.printStackTrace();  //TODO: Error handling.
				}
			}
		});
		thread.setName(name + "-Thread");
		thread.start();
	}
}
