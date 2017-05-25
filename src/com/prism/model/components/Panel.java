package com.prism.model.components;

import java.awt.Graphics2D;

import com.prism.model.Bounds;
import com.prism.model.Component;
import com.prism.model.Drawable;

public class Panel extends Component {

	private static final String STYLE = "panel";

	private Drawable drawable;

	public Panel(Bounds bounds) {
		super(bounds, STYLE);
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		drawable.render(graphics, width, height);
	}

	@Override
	public void update(int width, int height, Graphics2D graphics) {
		drawable = style.drawable(BACKGROUND);
	}

	@Override
	public boolean mouseEvent(float x, float y, int type, boolean control,
			boolean shift) {

		return false;
	}

	@Override
	public void state(int state) {
	}

	@Override
	public void overlay(Graphics2D graphics, int width, int height) {
	}

	@Override
	public boolean keyEvent(int key, boolean release, String text,
			boolean control, boolean shift) {
		return false;
	}

	@Override
	public int getMouseCursor(float x, float y) {
		return -1;
	}


	@Override
	public boolean dragged(int x, int y) {
		return false;
	}

	@Override
	public void hover(float x, float y) {

	}


}
