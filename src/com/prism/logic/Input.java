package com.prism.logic;

import com.prism.Window;

public interface Input {

	public void mouse(Window window, boolean click, int x, int y, boolean control, boolean shift, boolean hover);
	
	public void drag(Window window, int x, int y, boolean control, boolean shift);
	
	public void key(Window window, boolean press, int key, String text, boolean control, boolean shift);
	
}
