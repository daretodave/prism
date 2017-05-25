package com.prism.model;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import com.prism.Context;
import com.prism.Window;
import com.prism.cache.Cache;
import com.prism.listener.ActionListener;
import com.prism.listener.ChangeListener;
import com.prism.listener.StateListener;
import com.prism.model.Location.Hook;
import com.prism.model.Value.StaticValue;

public abstract class Component implements Thing {

	private static final String KEY_MAP     = "__KEYMAP__";

	protected static final String INITAL_PRESS_X = "_I_X_";
	protected static final String INITAL_PRESS_Y = "_I_Y_";

	public static final String BACKGROUND         = "background";
	public static final String BACKGROUND_FOCUSED = "background_focused";
	public static final String BACKGROUND_HOVERED = "background_hover";
	public static final String FORGROUND          = "forground";
	public static final String FORGROUND_FOCUSED  = "forground_focused";
	public static final String PADDING            = "padding";

	public static final String BLANK_STYLE = "blank";

	public static final int CHANGE = 0x0;

	public final Context context;
	public final Style style;
	public final State state;

	private int flags;

	private Location location;
	private Bounds bounds;
	private Component clip;
	private ThingList<Thing> children;


	private StateListener  stateListener;
	private ActionListener actionListener;
	private ChangeListener changeListener;

	private AffineTransform transform;
	private Animation animation;
	private Area area;

	public Component(Bounds bounds, String style) {
		this.bounds   = bounds;
		this.state    = new State();
		this.transform = new AffineTransform();
		this.location = new Location.Mold(0F, 0F);
		this.context  = new Context();
		this.children = new ThingList<Thing>(this);
		this.context.set(UPDATE, true);
		this.style = new Style("Component:"+getClass().getName());
		this.area = new Area();
		style(style);
	}

	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public void attach(Thing thing) {
		synchronized(children) {
			children.add(thing);
		}
	}

	@Override
	public void detach(Thing thing) {
		synchronized(children) {
			children.remove(thing);
		}
	}

	@Override
	public ThingList<Thing> children() {
		synchronized(children) {
			return children;
		}
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
	public float width() {
		float width = bounds.width();
		float maxw = context.$(MAX_WIDTH , Value.class, Value.MAX).getValue();
		float minw = context.$(MIN_WIDTH , Value.class, StaticValue.CONSTANTS[5]).getValue();
		width = Math.max(minw, width);
		width = Math.min(maxw, width);
		return width;
	}

	@Override
	public float height() {
		float height = bounds.height();
		float maxh = context.$(MAX_HEIGHT, Value.class, Value.MAX).getValue();
		float minh = context.$(MIN_HEIGHT, Value.class, StaticValue.CONSTANTS[5]).getValue();
		height = Math.max(minh, height);
		height = Math.min(maxh, height);
		return height;
	}

	@Override
	public void moveto(float x, float y) {
		if(location instanceof Location.Mold) {
			((Location.Mold) location).setX(x);
			((Location.Mold) location).setY(y);
		}
	}

	@Override
	public Bounds bounding() {
		return bounds;
	}

	@Override
	public void translate(float x, float y) {
		location.translate(x, y);
	}

	public void reset() {
		anim(null);
		state.setRotation(0F);
		state.setRotationx(.5F);
		state.setRotationy(.5F);
		state.setScalex(1F);
		state.setScalexx(.5F);
		state.setScalexy(.5F);
		state.setScaley(1F);
		state.setScaleyx(.5F);
		state.setScaleyy(.5F);
		state.setOpacity(1F);
		state.setTx(0F);
		state.setTy(0F);
	}

	public Animation anim(String anim) {
		return anim(anim, 1F);
	}

	public Animation anim(String anim, float modifier) {
		if(anim == null) {
			if(animation != null && !animation.isFinished() && animation.getListener() != null) {
				animation.getListener().onEnd(true);
			}
			this.animation = null;
		} else {
			this.animation = new Animation(Cache.sequence(anim), modifier);
		}
		return this.animation;
	}

	public void style(String style) {
		this.style.bridge(Cache.style(style));
	}

	public void style(String key, Object value) {
		style.set(key, value);
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

	public void displayBounds(Graphics2D graphics, boolean within) {
		if(!within)
			graphics.drawRect((int)x(), (int)y(), (int)width(), (int)height());
		else
			graphics.drawRect(0, 0, (int)width(), (int)height());
	}


	@Override
	public void keymap(int key, boolean release, Action action) {
		context.set(KEY_MAP+key+release, action);
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
	public boolean contains(float x, float y) {
		int w = (int) width();
		int h = (int) height();
		Area a = new Area(new Rectangle(0, 0, w, h));
		a.transform(transform);

		return a.contains(x, y);
	}

	public boolean contains(float x, float y, float width, float height, boolean intersects) {
		int w = (int) width();
		int h = (int) height();
		if(intersects) {
			return area.intersects(x, y, width, height) || area.contains(x, y, width, height);
		}
		return area.contains(x, y, width, height);
	}

	@Override
	public void render(Window window, Graphics2D graphics, int width, int height, long delta) {
		if(context.is(HIDDEN)) {
			return;
		}
		Shape prior = graphics.getClip();
		if(clip != null) {
			if(!clip.contains(x(), y(), width(), height(), true)) {
				return;
			}
			graphics.clipRect((int)clip.x(), (int)(clip.y() ), (int)clip.width(), (int)clip.height());
		}
		Composite previous = graphics.getComposite();
		if(previous == null || !(previous instanceof AlphaComposite) || ((AlphaComposite) previous).getAlpha() > state.getOpacity()) {
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, state.getOpacity()));
		}
		AffineTransform perserved = graphics.getTransform();
		if(animation != null) {
			animation.animate(this, delta);
		}
		graphics.translate(state.getTx(), state.getTy());
		graphics.translate(x(), y());
		int w = (int) width();
		int h = (int) height();
		double rx = w * state.getRotationx();
		double ry = h * state.getRotationy();
		graphics.translate(rx, ry);
		graphics.rotate(Math.toRadians(state.getRotation()));
		graphics.translate(-rx, -ry);
		rx = w * state.getScalexx();
		ry = h * state.getScalexy();
		graphics.translate(rx, ry);
		graphics.scale(state.getScalex(), 1F);
		graphics.translate(-rx, -ry);
		rx = w * state.getScaleyx();
		ry = h * state.getScaleyy();
		graphics.translate(rx, ry);
		graphics.scale(1F, state.getScaley());
		graphics.translate(-rx, -ry);
		transform = graphics.getTransform();
		area.reset();
		area.add(new Area(new Rectangle(0, 0, w, h)));
		area.transform(transform);
		draw(graphics, w, h);
		graphics.translate(-x(), -y());
		graphics.setClip(prior);
		children.render(window, graphics, width, height, delta);
		graphics.translate(x(), y());
		overlay(graphics, w, h);
		graphics.setClip(prior);
		graphics.setComposite(previous);
		graphics.setTransform(perserved);
	}

	@Override
	public void update(Graphics2D graphics) {
		update((int)width(), (int)height(), graphics);
	}

	public void update() {
		context.set(Thing.UPDATE, true);
	}

	public abstract void draw(Graphics2D graphics, int width, int height);

	public abstract void overlay(Graphics2D graphics, int width, int height);

	public abstract void update(int width, int height, Graphics2D graphics);

	public abstract boolean mouseEvent(float x, float y, int type, boolean control, boolean shift);

	public abstract boolean keyEvent(int key, boolean release, String text, boolean control, boolean shift);

	@Override
	public boolean drag(int x, int y) {
		if(context.is(PRESSED)) {
			boolean dragged = dragged(x, y);
			if(dragged) {
				return true;
			}
		}
		return children.drag(x, y);
	}

	public boolean within(float x, float y, int[] bounds) {
		return x >= bounds[0] && x <= bounds[2]+bounds[0] && y >= bounds[1] && y <= bounds[1] + bounds[3];
	}

	public abstract boolean dragged(int x, int y);

	@Override
	public boolean mouse(float x, float y, int type, boolean ctrl, boolean shift, boolean hover) {
		if(!hover && contains(x, y)) {
			if(type == Thing.PRESS) {
				context.set(INITAL_PRESS_X, x);
				context.set(INITAL_PRESS_Y, y);
			}
			boolean solved =  mouseEvent(x, y, type, ctrl, shift);
			if(solved) {
				return true;
			}
		}
		return children.mouse(x, y, type, ctrl, shift, hover);
	}

	@Override
	public boolean key(int key, boolean release, String text, boolean ctrl, boolean shift) {
		if(context.is(FOCUSED)) {
			if(release) {
				Action action = context.$(KEY_MAP+key+release, Action.class);
				if(action != null) {
					action.act(key, release, text, ctrl, shift);
					return true;
				}
			}
			boolean solved = children.key(key, release, text, ctrl, shift);
			if(solved) {
				return true;
			}
			return keyEvent(key, release, text, ctrl, shift);
		}
		return false;
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
	public void locate(Location location) {
		this.location = location;
	}

	@Override
	public void bound(Bounds bound) {
		this.bounds = bound;
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

	public void clip(Component clip) {
		this.clip = clip;
	}

	@Override
	public void hide(boolean hidden) {
		context.set(HIDDEN, hidden);
	}

	@Override
	public void tostate(int state) {
		state(state);
		if(stateListener != null) {
			stateListener.state(state);
		}
	}

	public void action(int action) {
		if(actionListener != null) {
			actionListener.action(action);
		}
	}

	public void change(int method) {
		if(changeListener != null) {
			changeListener.change(CHANGE);
			changeListener.change(method);
		}
	}

	public abstract void state(int state);

	public void tabto(final Thing element) {
		keymap(KeyEvent.VK_TAB, true, new Action() {
			@Override
			public void act(Object... elements) {
				if(!element.getContext().is(NEVER_FOCUSED)) {
					context.set(Thing.FOCUSED, false);
					tostate(Thing.UN_FOCUS);
					element.getContext().set(Thing.FOCUSED, true);
					element.tostate(Thing.FOCUS);
				}
			}
		});
	}

	public void enable(int flag) {
		if ((flags & flag) != flag) {
			flags |= flag;
		}
	}

	public void disable(int flag) {
		if ((flags & flag) == flag) {
			flags &= ~flag;
		}
	}

	public boolean is(int flag) {
		return (flags & flag) == flag;
	}

	public StateListener getStateListener() {
		return stateListener;
	}

	public void setStateListener(StateListener stateListener) {
		this.stateListener = stateListener;
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	public ChangeListener getChangeListener() {
		return changeListener;
	}

	public void setChangeListener(ChangeListener changeLIstener) {
		this.changeListener = changeLIstener;
	}

	public void setTranslate(float sx, float sy) {
		location.setTranslate(sx, sy);
	}

}
