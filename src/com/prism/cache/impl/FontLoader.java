package com.prism.cache.impl;

import java.awt.Font;
import java.io.InputStream;

import com.prism.cache.Loader;

public class FontLoader implements Loader<Font> {

	@Override
	public Font load(InputStream input, String title) throws Exception {
		return Font.createFont(Font.TRUETYPE_FONT, input);
	}

}
