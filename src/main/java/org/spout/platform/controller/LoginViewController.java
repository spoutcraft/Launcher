/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import org.spout.platform.tasks.GravatarTask;
import org.spout.platform.tasks.LoginTask;
import org.spout.platform.util.DesktopUtils;

public class LoginViewController extends ContentController {
	@FXML
	AnchorPane root;
	@FXML
	PasswordField password;
	@FXML
	TextField username;
	@FXML
	Button loginbutton;
	@FXML
	ProgressIndicator progress;
	@FXML
	Label errorLabel;
	@FXML
	Rectangle loginBackground;
	@FXML
	VBox loginBox;
	@FXML
	StackPane centerPane;

	@Override
	public Node getRootNode() {
		return root;
	}

	@FXML
	public void initialize() {
		loginBackground.widthProperty().bind(loginBox.widthProperty().add(50d));
		loginBackground.heightProperty().bind(loginBox.heightProperty().add(50d));
	}

	@Override
	public void onActivate() {
		super.onActivate();
		loginbutton.setDefaultButton(true);
		progress.setVisible(false);
		setDisableControls(false);
		errorLabel.setVisible(false);
		for (final ContentController controller : applicationController.getControllers()) {
			controller.navigationButton.setVisible(false);
		}
	}

	@Override
	public String getName() {
		return "Login";
	}

	@FXML
	public void onLoginAction(ActionEvent event) {
		errorLabel.setVisible(false);
		final String user = username.getText();
		final String pass = password.getText();

		if (user.isEmpty() || pass.isEmpty()) {
			showErrorMessage("Username or password missing");
			return;
		}

		progress.setVisible(true);
		loginbutton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		setDisableControls(true);

		final LoginTask task = new LoginTask(applicationController.getUser(), user, pass);
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				onLoggedIn();
			}
		});
		task.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				onLoginError(task.getException());
			}
		});

		applicationController.getExecutorService().submit(task);
	}

	public void onLoggedIn() {
		final LoginViewController loginController = this;

		applicationController.getExecutorService().submit(new GravatarTask(applicationController.getUser()));

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("Logged in!");
				progress.setVisible(false);
				loginbutton.setContentDisplay(ContentDisplay.CENTER);

				boolean firstConsumed = false;
				for (final ContentController controller : applicationController.getControllers()) {
					if (controller != loginController) {
						controller.navigationButton.setVisible(true);
						final FadeTransition transition = new FadeTransition(Duration.seconds(1), controller.navigationButton);
						transition.setFromValue(0d);
						transition.setToValue(1d);
						transition.play();
						if (!firstConsumed) {
							firstConsumed = true;
							applicationController.activateController(controller);
						}
					}
				}
				((Pane) navigationButton.getParent()).getChildren().remove(navigationButton);
			}
		});
	}

	public void onLoginError(final Throwable throwable) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				showErrorMessage(throwable.getMessage());
				setDisableControls(false);
				progress.setVisible(false);
			}
		});
	}

	public void showErrorMessage(String error) {
		errorLabel.setVisible(true);
		errorLabel.setText(error);
	}

	public void setDisableControls(boolean disable) {
		password.setDisable(disable);
		username.setDisable(disable);
		loginbutton.setDisable(disable);
	}

	@FXML
	public void onRegisterAction(ActionEvent event) {
		DesktopUtils.openUrl("https://my.spout.org/register");
	}

	@FXML
	public void onForgotPasswordAction(ActionEvent event) {
		DesktopUtils.openUrl("https://my.spout.org/forgot"); // TODO: Check if this is correct.
	}
}
