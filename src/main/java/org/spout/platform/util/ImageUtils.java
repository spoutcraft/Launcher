/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import org.apache.commons.io.IOUtils;

public class ImageUtils {
	public static BufferedImage scaleImage(BufferedImage img, int width, int height) {
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		if (imgWidth * height < imgHeight * width) {
			width = imgWidth * height / imgHeight;
		} else {
			height = imgHeight * width / imgWidth;
		}
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		try {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(img, 0, 0, width, height, null);
		} finally {
			g.dispose();
		}
		return newImage;
	}

	public static BufferedImage getImageSafely(String path) {
		InputStream stream = null;
		try {
			stream = ResourceUtils.getResourceAsStream(path);
			return ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
	}

	public static Image getFXImageSafely(String path) {
		InputStream stream = null;
		try {
			stream = ResourceUtils.getResourceAsStream(path);
			return new Image(stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
}
