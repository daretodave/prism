package com.prism.model;

import java.awt.Graphics2D;

import com.prism.Context;
import com.prism.Window;
import com.prism.model.Location.Hook;

public class Region implements Thing {

	private Location location;
	private Bounds bounds;
	private Context context;
	private ThingList<Thing> children;

	public static final String DEBUG = "__DEBUG";

	public Region(Bounds bounds) {
		this.bounds   = bounds;
		this.location = new Location.Mold(10F, 10F);
		this.context  = new Context();
		this.children = new ThingList<Thing>(this);
	}

	@Override
	public void bound(Bounds bound) {
		this.bounds = bound;
	}

	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public void update(Graphics2D graphics) {
	}

	@Override
	public void attach(Thing thing) {
		children.add(thing);
	}

	@Override
	public void detach(Thing thing) {
		children.remove(thing);
	}

	@Override
	public ThingList<Thing> children() {
		return children;
	}

	@Override
	public float x() {
		return location.x();
	}

	@Override
	public float y() {
		return location.y();
	}

	@Override
	public float outerx() {
		return x() + width();
	}

	@Override
	public float outery() {
		return y() + height();
	}

	@Override
	public float width() {
		return bounds.width();
	}

	@Override
	public float height() {
		return bounds.height();
	}

	@Override
	public void moveto(float x, float y) {
		if(location instanceof Location.Mold) {
			((Location.Mold) location).setX(x);
			((Location.Mold) location).setY(y);
		}
	}

	@Override
	public void translate(float x, float y) {
		location.translate(x, y);
	}

	@Override
	public void size(float width, float height) {
		if(bounds instanceof Bounds.Mold) {
			((Bounds.Mold) bounds).setWidth(width);
			((Bounds.Mold) bounds).setHeight(height);
		}
	}

	@Override
	public void grow(float w, float h) {
		bounds.translate(w, h);
	}

	@Override
	public boolean mouse(float x, float y, int type, boolean ctrl,
			boolean shift, boolean hover) {
		return children.mouse(x, y, type, ctrl, shift, hover);
	}

	@Override
	public boolean key(int key, boolean release, String text, boolean ctrl,
			boolean shift) {
		return children.key(key, release, text, ctrl, shift);
	}

	@Override
	public void render(Window window, Graphics2D graphics, int width, int height, long delta) {
		if(context.is(DEBUG)) {
			graphics.setColor(java.awt.Color.RED);
			graphics.drawRect((int)x(), (int)y(), width, height);
		}
		children.render(window, graphics, width, height, delta);
	}

	@Override
	public void keymap(int key, boolean release, Action action) {

	}

	@Override
	public void tostate(int state) {

	}

	@Override
	public void locate(Thing other, Hook where) {
		this.location = new Location.Relative(this, other, where, where);
	}

	@Override
	public void locate(Hook on, Thing other, Hook where) {
		this.location = new Location.Relative(this, other, on, where);
	}

	@Override
	public boolean contains(float x, float y) {
		if(x >= x() && x <= outerx() && y >= y() && y<= outery()) {
			return true;
		}
		return false;
	}

	@Override
	public void locate(Location location) {
		this.location = location;
	}

	@Override
	public Bounds bounding() {
		return bounds;
	}

	@Override
	public int getMouseCursor(float x, float y) {
		return -1;
	}

	@Override
	public void minwidth(Value value) {
		context.set(MIN_WIDTH, value);
	}

	@Override
	public void maxwidth(Value value) {
		context.set(MAX_WIDTH, value);
	}

	@Override
	public void minheight(Value value) {
		context.set(MIN_HEIGHT, value);
	}

	@Override
	public void maxheight(Value value) {
		context.set(MAX_HEIGHT, value);
	}

	@Override
	public void minwidth(float width) {
		minwidth(new Value.StaticValue(width));
	}

	@Override
	public void maxwidth(float width) {
		maxwidth(new Value.StaticValue(width));
	}

	@Override
	public void minheight(float height) {
		minheight(new Value.StaticValue(height));
	}

	@Override
	public void maxheight(float height) {
		minheight(new Value.StaticValue(height));
	}

	@Override
	public void hide(boolean hidden) {
	}

	@Override
	public boolean drag(int x, int y) {
		return false;
	}

	@Override
	public void hover(float x, float y) {

	}


}
