package com.prism.logic;

import java.awt.Graphics2D;

import com.prism.Window;

public interface Renderer {
	
	public void render(Window window, Graphics2D graphic, int width, int height);
	
}
