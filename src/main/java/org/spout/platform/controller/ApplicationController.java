/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;

import com.cathive.fx.guice.FXMLController;
import com.cathive.fx.guice.GuiceFXMLLoader;
import com.cathive.fx.guice.GuiceFXMLLoader.Result;
import com.narrowtux.fxdecorate.FxDecorateScene;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import org.spout.platform.gui.Views;
import org.spout.platform.gui.font.FontAwesome;
import org.spout.platform.model.User;

@FXMLController
public class ApplicationController {
	private static AtomicInteger threadCount = new AtomicInteger(0);
	private User user;
	private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName("Executor #" + threadCount.getAndIncrement());
			t.setDaemon(true);
			return t;
		}
	});
	@FXML
	private HBox navigationButtons;
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane content;
	@FXML
	private ImageView avatar;
	@FXML
	private ImageView spoutLogo;
	@FXML
	private Label username;
	@FXML
	private Label avatarDefault;
	@FXML
	private Label closeButtonIcon;
	@FXML
	private Label maxButtonIcon;
	@FXML
	private Label minButtonIcon;
	@FXML
	private HBox navigationHBox;
	@FXML
	private Rectangle navigationBackground;
	@FXML
	private Button notifications;
	@Inject
	private GuiceFXMLLoader fxmlLoader;
	private boolean maximized = false;
	private Bounds savedBounds = null;
	private NotificationController notificationController;
	private Set<String> subViews = new LinkedHashSet<String>() {
		private static final long serialVersionUID = 1L;

		{
			//add(Views.LOGIN_VIEW);
			add(Views.DASHBOARD_VIEW);
			add(Views.WEB_VIEW);
		}
	}; // Sub views of the Application
	private List<ContentController> controllers = new ArrayList<ContentController>();
	private ContentController activeController = null;
	private FxDecorateScene fxDecorateScene;

	@FXML
	public void initialize() {
		user = new User();
		navigationBackground.widthProperty().bind(root.widthProperty());
		loadSubControllers();
		username.textProperty().bind(user.usernameProperty());
		avatar.imageProperty().bind(user.avatar());

		FontAwesome.stylise(avatarDefault);
		avatarDefault.setText(FontAwesome.ICON_USER);

		FontAwesome.stylise(closeButtonIcon);
		closeButtonIcon.setText(FontAwesome.ICON_REMOVE);
		FontAwesome.stylise(maxButtonIcon);
		maxButtonIcon.setText(FontAwesome.ICON_CHECK_EMPTY);
		FontAwesome.stylise(minButtonIcon);
		minButtonIcon.setText(FontAwesome.ICON_MINUS);
		try {
			Result chatResult = fxmlLoader.load(ApplicationController.class.getResource(Views.CHAT_VIEW));
			Scene scene = new Scene((Parent) chatResult.getRoot());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	/**
	 * Sets the active Controller in the content area.
	 *
	 * @param contentController the new active controller.
	 * @param animate wether the change of controllers should be animated.
	 */
	void activateController(final ContentController contentController, boolean animate) {
		if (activeController == contentController) {
			return;
		}
		int from = controllers.indexOf(activeController);
		int to = controllers.indexOf(contentController);
		final ContentController oldController = activeController;
		contentController.getRootNode().setVisible(true);
		if (activeController != null) {
			//activeController.getRootNode().setVisible(false);
			activeController.onDeactivate();
			activeController.getNavigationButton().getStyleClass().remove("selected");
		}
		activeController = contentController;
		activeController.getNavigationButton().getStyleClass().add("selected");
		int direction = from < to ? -1 : 1;
		if (animate && oldController != null) {
			animateController(contentController, oldController, direction);
		}
		activeController.onActivate();
	}

	private void animateController(final ContentController contentController, final ContentController oldController, int direction) {
		oldController.getRootNode().setVisible(true);
		if (activeController.getRootNode() instanceof Pane) {
			((Pane) activeController.getRootNode()).setPrefSize(content.getWidth(), content.getHeight());
		}
		if (oldController.getRootNode() instanceof Pane) {
			((Pane) oldController.getRootNode()).setPrefSize(content.getWidth(), content.getHeight());
		}
		AnchorPane.clearConstraints(activeController.getRootNode());
		AnchorPane.clearConstraints(oldController.getRootNode());
		final Timeline timeline = new Timeline();
		timeline.setCycleCount(1);
		KeyValue kvNew = new KeyValue(activeController.getRootNode().layoutXProperty(), content.getWidth() * -direction);
		KeyValue kvNewOpac = new KeyValue(activeController.getRootNode().opacityProperty(), 0.0);
		KeyFrame kf = new KeyFrame(Duration.ZERO, kvNew, kvNewOpac);
		timeline.getKeyFrames().add(kf);
		KeyValue kvOld = new KeyValue(oldController.getRootNode().layoutXProperty(), content.getWidth() * direction);
		kvNew = new KeyValue(activeController.getRootNode().layoutXProperty(), 0);
		kvNewOpac = new KeyValue(activeController.getRootNode().opacityProperty(), 1.0);
		KeyValue kvOldOpac = new KeyValue(oldController.getRootNode().opacityProperty(), 0.0);
		kf = new KeyFrame(Duration.millis(200), kvOld, kvNew, kvNewOpac, kvOldOpac);
		timeline.getKeyFrames().add(kf);
		timeline.play();
		timeline.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				setControllerConstrains(oldController);
				setControllerConstrains(contentController);
				oldController.getRootNode().setVisible(false);
			}
		});
	}

	void activateController(ContentController contentController) {
		activateController(contentController, true);
	}

	private void loadSubControllers() {
		for (final String subview : subViews) {
			loadSubController(subview);
		}
		activateController(controllers.get(0));
	}

	public void loadSubController(final String subController) {
		try {
			final Result loadResult = fxmlLoader.load(ApplicationController.class.getResource(subController));
			final ContentController controller = loadResult.getController();
			controllers.add(controller);
			controller.setApplicationController(this);
			final Button btn = addControllerButton(controller);
			controller.setNavigationButton(btn);
			content.getChildren().add(controller.getRootNode());
			setControllerConstrains(controller);
			controller.getRootNode().setVisible(false);
		} catch (final IOException e) {
			throw new RuntimeException("Could not load sub view: " + subController, e);
		}
	}

	protected void setControllerConstrains(final ContentController controller) {
		AnchorPane.setTopAnchor(controller.getRootNode(), 0d);
		AnchorPane.setLeftAnchor(controller.getRootNode(), 0d);
		AnchorPane.setRightAnchor(controller.getRootNode(), 0d);
		AnchorPane.setBottomAnchor(controller.getRootNode(), 0d);
	}

	private Button addControllerButton(final ContentController controller) {
		final Button navButton = new Button(controller.getName()); // TODO: This should be some kind of custom button/label later.
		navButton.getStyleClass().add("nav-button");
		navButton.setPrefHeight(49);

		navButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				activateController(controller);
			}
		});
		navigationButtons.getChildren().add(navButton);

		return navButton;
	}

	public User getUser() {
		return user;
	}

	public List<ContentController> getControllers() {
		return controllers;
	}

	public void onQuit() {
		for (final ContentController controller : getControllers()) {
			try {
				controller.onQuit();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onCloseButton() {
		fxDecorateScene.getStage().fireEvent(new WindowEvent(fxDecorateScene.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	public void onMinButton() {
		fxDecorateScene.getStage().setIconified(true);
	}

	public void onMaxButton() {
		Stage stage = fxDecorateScene.getStage();
		if (maximized) {
			double x, y, width, height;
			x = savedBounds.getMinX();
			y = savedBounds.getMinY();
			width = savedBounds.getWidth();
			height = savedBounds.getHeight();
			stage.setX(x);
			stage.setY(y);
			stage.setWidth(width);
			stage.setHeight(height);
			stage.setResizable(true);
			maximized = false;
		} else {
			savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
			ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
			Screen screen = screensForRectangle.get(0);
			Rectangle2D visualBounds = screen.getVisualBounds();

			stage.setX(visualBounds.getMinX());
			stage.setY(visualBounds.getMinY());
			stage.setWidth(visualBounds.getWidth());
			stage.setHeight(visualBounds.getHeight());

			maximized = true;
		}
	}

	public void onNotificationButtonPressed() {
		Stage notif = getNotificationController().getStage();
		if (notif.isShowing()) {
			System.out.println("Hiding .");
			if (!getNotificationController().animating) {
				getNotificationController().hide();
			}
		}
	}

	public void onNotificationButtonReleased() {
		Stage notif = getNotificationController().getStage();
		if (!notif.isShowing()) {
			Bounds b = notifications.localToScene(notifications.getLayoutBounds());
			double x = (b.getMinX() + b.getMaxX()) / 2d;
			double y = b.getMaxY();
			x += getDecorateScene().getStage().getX();
			y += getDecorateScene().getStage().getY();

			Point2D arrow = getNotificationController().getArrowPosition();
			x -= arrow.getX();
			y -= arrow.getY();

			notif.setX(x);
			notif.setY(y);

			getNotificationController().show();
		}
	}

	public void setDecorateScene(FxDecorateScene fxDecorateScene) {
		this.fxDecorateScene = fxDecorateScene;

		fxDecorateScene.getController().addMoveNode(navigationBackground);
		fxDecorateScene.getController().addMoveNode(spoutLogo);
		fxDecorateScene.getController().addMoveNode(username);
		fxDecorateScene.getController().addMoveNode(avatar);
	}

	public FxDecorateScene getDecorateScene() {
		return fxDecorateScene;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setNotificationController(NotificationController notificationController) {
		this.notificationController = notificationController;
	}

	public NotificationController getNotificationController() {
		return notificationController;
	}
}
