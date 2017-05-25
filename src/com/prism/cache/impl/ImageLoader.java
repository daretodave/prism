package com.prism.cache.impl;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.prism.cache.Loader;

public class ImageLoader implements Loader<BufferedImage> {
	@Override
	public BufferedImage load(InputStream input, String title) throws Exception {
		return ImageIO.read(input);
	}
}