package com.prism.model;

import java.awt.Graphics2D;

import com.prism.Context;
import com.prism.Window;
import com.prism.model.Location.Hook;

public interface Thing {

	public static final int PRESS      = 0x0;
	public static final int RELEASE    = 0x1;
	public static final int HOVER      = 0x2;
	public static final int UN_HOVER   = 0x3;
	public static final int FOCUS      = 0x4;
	public static final int UN_FOCUS   = 0x5;
	public static final int RELEASE_IN_BOUNDS = 0x6;

	public static final String MIN_WIDTH  = "__MIN_WIDTH";
	public static final String MIN_HEIGHT = "__MIN_HEIGHT";
	public static final String MAX_WIDTH  = "__MAX_WIDTH";
	public static final String MAX_HEIGHT = "__MAX_HEIGHT";

	public static final String UPDATE  = "__UPDATE";
	public static final String HOVERED = "__HOVERED";
	public static final String PRESSED = "__PRESSED";
	public static final String FOCUSED = "__FOCUSED";

	public static final String HIDDEN         = "__HIDDEN";
	public static final String HIDE_CHILDREN  = "__HIDE_CHILDREN";
	public static final String ALWAYS_FOCUSED = "__ALWAYS_FOCUSED";
	public static final String NEVER_FOCUSED  = "__NEVER_FOCUSED";
	public static final String BACKGROUND_CANT_ROB_FOCUS = "__BG_CANT_ROB_FOCUS";
	public static final String NOT_FOCUSABLE_FROM_CLICK = "__NFFC";

	public Context getContext();

	public void attach(Thing thing);

	public void detach(Thing thing);

	public int getMouseCursor(float x, float y);

	public ThingList<Thing> children();

	public float x();

	public float y();

	public float outerx();

	public float outery();

	public float width();

	public float height();

	public void moveto(float x, float y);

	public void hover(float x, float y);

	public void translate(float x, float y);

	public void size(float width, float height);

	public void grow(float w, float h);

	public boolean mouse(float x, float y, int type, boolean ctrl, boolean shift, boolean hover);

	public boolean key(int key, boolean release, String text, boolean ctrl, boolean shift);

	public void render(Window window, Graphics2D graphics, int width, int height, long delta);

	public void keymap(int key, boolean release, Action action);

	public void tostate(int state);

	public void bound(Bounds bound);

	public void minwidth(Value value);

	public void maxwidth(Value value);

	public void minheight(Value value);

	public void maxheight(Value value);

	public void minwidth(float width);

	public void maxwidth(float width);

	public void minheight(float height);

	public void maxheight(float height);

	public void hide(boolean hidden);

	public Bounds bounding();

	public void locate(Location location);

	public void locate(Thing other, Hook where);

	public void locate(Hook on, Thing other, Hook where);

	public interface Action {

		public void act(Object...elements);
	}

	public boolean contains(float x, float y);

	public void update(Graphics2D graphics);

	public boolean drag(int x, int y);

}
