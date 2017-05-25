package com.prism.model;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.prism.Window;

@SuppressWarnings("serial")
public class ThingList<T extends Thing> extends ArrayList<T> {

	private Thing parent;

	public ThingList(Thing parent) {
		this.parent = parent;
	}

	public ThingList<T> collect(String key, Object value) {
		ThingList<T> sublist = new ThingList<T>(parent);
		for(T element : this) {
			if(element.getContext().is(key, value)) {
				sublist.add(element);
			}
		}
		return sublist;
	}

	public void render(Window window, Graphics2D graphics, int width, int height, long delta) {
		if(parent.getContext().is(Thing.HIDE_CHILDREN)) {
			return;
		}
		for(Thing element : this) {
			if(element.getContext().is(Thing.UPDATE)) {
				element.update(graphics);
				element.getContext().set(Thing.UPDATE, false);
			}
			try {
				element.render(window, graphics, (int)element.width(), (int)element.height(), delta);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	public int cursor(float x, float y) {
		for(Thing element : this) {
			int cursor = element.children().cursor(x, y);
			if(cursor != -1) {
				return cursor;
			}
			if(element.contains(x, y)) {
				cursor = element.getMouseCursor(x, y);
				if(cursor != -1) {
					return cursor;
				}
			}
		}
		return -1;
	}

	public boolean mouse(float x, float y, int type, boolean control, boolean shift, boolean hover) {
		ThingList<Thing> focused = null;
		if(type == Thing.PRESS) {
			focused = new ThingList<Thing>(parent);
			for(int i = size()-1; i >= 0; i--) {
				Thing element = get(i);
				if(element.contains(x, y) && !element.getContext().is(Thing.FOCUSED) && !element.getContext().is(Thing.NEVER_FOCUSED)) {
					focused.add(element);
				}
			}
		}
		for(int i = size()-1; i >= 0; i--) {
			Thing element = get(i);
			if(type == Thing.PRESS && element.getContext().is(Thing.FOCUSED) && !focused.contains(element)) {
				if(focused.isEmpty() && parent.getContext().is(Thing.BACKGROUND_CANT_ROB_FOCUS)) {

				} else {
					element.getContext().set(Thing.FOCUSED, false);
					element.tostate(Thing.UN_FOCUS);
					element.getContext().set(Thing.UPDATE, true);
				}
			}
			if(type == Thing.RELEASE && element.getContext().is(Thing.PRESSED)) {
				element.getContext().set(Thing.PRESSED, false);
				element.tostate(Thing.RELEASE);
				if(element.contains(x, y)) {
					element.tostate(Thing.RELEASE_IN_BOUNDS);
				}
			}
			if(!element.contains(x, y)) {
				if(element.getContext().is(Thing.HOVERED)) {
					element.getContext().set(Thing.HOVERED, false);
					element.tostate(Thing.UN_HOVER);
				}
				if(hover) {
					element.hover(x, y);
				}
			} else {
				if(!element.getContext().is(Thing.HOVERED)) {
					element.getContext().set(Thing.HOVERED, true);
					element.tostate(Thing.HOVER);
				}
				switch(type) {
				case Thing.PRESS:
					if(!element.getContext().is(Thing.PRESSED)) {
						element.getContext().set(Thing.PRESSED, true);
						element.tostate(Thing.PRESS);
					}
					if(!element.getContext().is(Thing.NEVER_FOCUSED) && !element.getContext().is(Thing.NOT_FOCUSABLE_FROM_CLICK)) {
						element.getContext().set(Thing.FOCUSED, true);
						element.tostate(Thing.FOCUS);
					}
					break;
				}
			}
		}
		for(int i = size()-1; i >= 0; i--) {
			Thing element = get(i);
			boolean solved = element.mouse(x, y, type, control, shift, hover);
			if(solved) {
				return true;
			}
		}
		return false;
	}

	public boolean key(int key, boolean release, String text,
			boolean ctrl, boolean shift) {
		for(int i = size()-1; i >= 0; i--) {
			Thing element = get(i);
			boolean solved = element.key(key, release, text, ctrl, shift);
			if(solved) {
				return true;
			}
		}
		return false;
	}

	public boolean drag(int x, int y) {
		for(Thing element : this) {
			if(element.drag(x, y)) {
				return true;
			}
		}
		return false;
	}

	public float height() {
		float height = 0F;
		for(Thing element : this) {
			height += element.height();
		}
		return height;
	}

}
