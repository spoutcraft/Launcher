/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.spout.platform.model.Notification;

public class NotificationController {
	private Stage stage;
	@FXML
	private Rectangle arrow;
	@FXML
	private Rectangle titleBar;
	@FXML
	private ListView<Notification> notificationView;
	@FXML
	private AnchorPane content;
	private WritableValue<Double> stageHeight;
	boolean animating = false;

	public void init() {
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obj, Boolean oldv, Boolean newv) {
				if (!newv) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (!animating) {
								hide();
							}
						}
					});
				}
			}
		});

		getStage().getScene().setFill(null);

		stageHeight = new WritableValue<Double>() {
			@Override
			public Double getValue() {
				return stage.getHeight();
			}

			@Override
			public void setValue(Double arg0) {
				stage.setHeight(arg0);
			}
		};
	}

	public Point2D getArrowPosition() {
		Bounds b = arrow.localToScene(arrow.getLayoutBounds());
		double y = b.getMinY();
		double x = (b.getMinX() + b.getMaxX()) / 2d;
		return new Point2D(x, y);
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Stage getStage() {
		return stage;
	}

	public void show() {
		if (stage.isShowing() || animating) {
			stage.show();
			return;
		}
		animating = true;
		double startHeight = titleBar.localToScene(titleBar.layoutBoundsProperty().get()).getMaxY();
		double finalHeight = 350;

		Timeline timeline = new Timeline();
		KeyValue titleOpacity = new KeyValue(stage.opacityProperty(), 0.0);
		KeyValue height = new KeyValue(stageHeight, startHeight);
		KeyFrame zero = new KeyFrame(Duration.ZERO, titleOpacity, height);
		titleOpacity = new KeyValue(stage.opacityProperty(), 1.0);
		height = new KeyValue(stageHeight, startHeight);
		KeyFrame fadeIn = new KeyFrame(Duration.millis(200), titleOpacity, height);
		height = new KeyValue(stageHeight, finalHeight);
		KeyFrame slide = new KeyFrame(Duration.millis(400), height);
		timeline.getKeyFrames().addAll(zero, fadeIn, slide);
		timeline.play();
		stage.show();
		timeline.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				animating = false;
			}
		});
	}

	public void hide() {
		System.out.println("Hiding..." + animating);
		if (!stage.isShowing() || animating) {
			stage.hide();
			return;
		}
		animating = true;

		stage.hide();
		stage.show();

		double startHeight = titleBar.localToScene(titleBar.layoutBoundsProperty().get()).getMaxY();
		final double finalHeight = stage.getHeight();

		Timeline timeline = new Timeline();
		KeyValue height = new KeyValue(stageHeight, startHeight);
		KeyFrame slide = new KeyFrame(Duration.millis(200), height);
		KeyValue titleOpacity = new KeyValue(stage.opacityProperty(), 0.0);
		KeyFrame fadeOut = new KeyFrame(Duration.millis(400), titleOpacity);
		timeline.getKeyFrames().addAll(slide, fadeOut);
		timeline.play();
		timeline.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				stage.hide();
				stage.setHeight(finalHeight);
				animating = false;
			}
		});
	}
}
