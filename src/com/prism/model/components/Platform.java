package com.prism.model.components;

import java.awt.Graphics2D;

import com.prism.model.Bounds;
import com.prism.model.Component;

public class Platform extends Component {

	public Platform(Bounds bounds) {
		super(bounds, BLANK_STYLE);
	}

	@Override
	public int getMouseCursor(float x, float y) {
		
		return -1;
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		
		
	}

	@Override
	public void overlay(Graphics2D graphics, int width, int height) {
		
		
	}

	@Override
	public void update(int width, int height, Graphics2D graphics) {
		
		
	}

	@Override
	public boolean mouseEvent(float x, float y, int type, boolean control,
			boolean shift) {
		
		return false;
	}

	@Override
	public boolean keyEvent(int key, boolean release, String text,
			boolean control, boolean shift) {
		
		return false;
	}

	@Override
	public boolean dragged(int x, int y) {
		
		return false;
	}

	@Override
	public void state(int state) {
		
		
	}

	@Override
	public void hover(float x, float y) {
		
	}

}
