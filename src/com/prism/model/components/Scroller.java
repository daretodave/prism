package com.prism.model.components;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;

import com.prism.model.Bounds;
import com.prism.model.Component;
import com.prism.model.Drawable;
import com.prism.model.Location.Hook;
import com.prism.model.Thing;

public class Scroller extends Component {

	private Component scrolled;

	private float sx;
	private float sy;

	private int[] sxbounds;
	private int[] sybounds;

	public static final int NO_SCROLL_X = 1;
	public static final int NO_SCROLL_Y = 2;

	public static final String STYLE = "scroller";

	public static final String ACCEL_X = "accelx";
	public static final String ACCEL_Y = "accely";
	public static final String MIN_BAR_SIZE = "minbarsize";
	public static final String BAR_SIZE     = "barsize";

	private boolean lockx;
	private boolean locky;

	private float[] startx;
	private float[] starty;

	private float padding;
	private float accelx;
	private float accely;
	private float size;
	private float min;

	private Drawable background;
	private Drawable focused;
	//BACKGROUND, BACKGROUND_PRESS

	public Scroller(Bounds bounds) {
		super(bounds, STYLE);
		sxbounds = new int[4];
		sybounds = new int[4];
		startx   = new float[2];
		starty   = new float[2];
	}

	public void scroll(Component thing) {
		if(scrolled != null) {
			detach(scrolled);
			scrolled.clip(null);
		}
		scrolled = thing;
		scrolled.locate(this, Hook.NW);
		scrolled.clip(this);
		attach(scrolled);
	}

	@Override
	public int getMouseCursor(float x, float y) {
		if((!is(NO_SCROLL_Y) && within(x, y, sybounds)) || (!is(NO_SCROLL_X) && within(x, y, sxbounds))) {
			return Cursor.HAND_CURSOR;
		}
		return -1;
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
	}

	@Override
	public void overlay(Graphics2D graphics, int width, int height) {
		float owidth  = scrolled.width()  - width();
		float oheight = scrolled.height() - height();
		Shape prior = graphics.getClip();
		graphics.clipRect(0, 0, width+2, height+2);
		graphics.setColor(java.awt.Color.RED);
		if(oheight > 0 && !is(NO_SCROLL_Y)) {
			float bheight  = Math.max(min, (height()/scrolled.height()) * height());
			float overall = scrolled.height() - height();
			int y = (int) (Math.abs(sy/overall) * (height() - bheight - padding*2));
			sybounds[0] = (int) (width - size);
			sybounds[1] = (int) (y + padding);
			sybounds[2] = (int) size;
			sybounds[3] = (int) bheight;
			(!locky ? background : focused).render(graphics, sybounds[0], sybounds[1], sybounds[2]-2, sybounds[3]);
		}
		if(owidth > 0 && !is(NO_SCROLL_X)) {
			float bwidth  = Math.max(min, (width()/scrolled.width()) * width());
			float overall = scrolled.width() - width();
			int x = (int) (Math.abs(sx/overall) * (width() - bwidth - padding*2));
			sxbounds[0] = (int) (x + padding);
			sxbounds[1] = (int) (height-size);
			sxbounds[2] = (int) bwidth;
			sxbounds[3] = (int) size;
			(!lockx ? background : focused).render(graphics, sxbounds[0], sxbounds[1], sxbounds[2], sxbounds[3]-2);
		}
		graphics.setClip(prior);
		normalize();
	}

	@Override
	public void update(int width, int height, Graphics2D graphics) {
		accelx  = style.number(ACCEL_X);
		accely  = style.number(ACCEL_Y);
		padding = style.number(PADDING);
		min     = style.number(MIN_BAR_SIZE);
		size    = style.number(BAR_SIZE);
		background = style.drawable(BACKGROUND);
		focused = style.drawable(BACKGROUND_FOCUSED);
	}

	@Override
	public boolean mouseEvent(float x, float y, int type, boolean control,
			boolean shift) {
		switch(type) {
		case Thing.PRESS:
			if(within(x, y, sxbounds)  && !is(NO_SCROLL_X)) {
				lockx  = true;
				startx[0] = x;
				startx[1] = sxbounds[0];
			}
			if(within(x, y, sybounds)  && !is(NO_SCROLL_Y)) {
				locky  = true;
				starty[0] = y;
				starty[1] = sybounds[1];
			}
			break;
		}
		return false;
	}



	@Override
	public boolean keyEvent(int key, boolean release, String text,
			boolean control, boolean shift) {
		if(!release) {
			switch(key) {
			case KeyEvent.VK_RIGHT:
				scroll(-accelx, 0F);
				break;
			case KeyEvent.VK_LEFT:
				scroll(accelx, 0F);
				break;
			case KeyEvent.VK_UP:
				scroll(0F, accely);
				break;
			case KeyEvent.VK_DOWN:
				scroll(0F, -accely);
				break;
			case KeyEvent.VK_ENTER:
				anim("shake");
				break;
			}
		}
		return true;
	}

	private void scroll(float x, float y) {
		if(scrolled != null) {
			this.sx += x;
			this.sy += y;
			normalize();
		}
	}

	private void normalize() {
		this.sx = Math.max(sx, -scrolled.width()  + width());
		this.sy = Math.max(sy, -scrolled.height() + height());
		this.sx = Math.min(0, sx);
		this.sy = Math.min(0, sy);
		scrolled.setTranslate(sx, sy);
	}

	@Override
	public void state(int state) {
		switch(state) {
		case Thing.RELEASE:
			lockx = false;
			locky = false;
		case Thing.PRESS:
			update();
			break;
		}
	}

	@Override
	public boolean dragged(int x, int y) {
		if(lockx) {
			float dx = startx[0]-x;
			float px = startx[1]-dx;
			float bwidth  = Math.max(min, (width()/scrolled.width()) * width());
			sx = (px * (scrolled.width() - width())) / -(width() - bwidth - 2*padding);
			normalize();
		}
		if(locky) {
			float dy = starty[0]-y;
			float py = starty[1]-dy;
			float bheight  = Math.max(min, (height()/scrolled.height()) * height());
			sy = (py * (scrolled.height() - height())) / -(height() - bheight - 2*padding);
			normalize();
		}
		return lockx || locky;
	}

	@Override
	public void hover(float x, float y) {

	}
}
