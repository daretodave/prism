package com.prism.model;

import java.awt.Graphics2D;

import com.prism.Context;

public abstract class Drawable {
	
	public static final String RADIUS 		 = "radius";
	public static final String DISPLAY_INNER = "display_inner";
	public static final String BORDER 	     = "border";
	public static final String BORDER_COLOR  = "border_color";
	
	private Drawable overlay;
	private Drawable underlay;
	
	private Context style = new Context();
	
	protected abstract void draw(Graphics2D graphics, int x, int y, int width, int height, Context context);
	
	public abstract Drawable copy();

	public void underlay(Drawable underlay) {
		this.underlay = underlay;
	}
	
	public void overlay(Drawable overlay) {
		this.overlay = overlay;
	}
	
	public void style(Context context) {
		this.style = context;
	}
	
	public void set(String key, Object value) {
		style.set(key, value);
	}
	
	public void render(Graphics2D graphics, int width, int height) {
		render(graphics, 0, 0, width, height);
	}
	
	public void render(Graphics2D graphics, int x, int y, int width, int height) {
		if(underlay != null) {
			underlay.draw(graphics, x, y, width, height, style);
		}
		draw(graphics, x, y, width, height, style);
		if(overlay != null) {
			overlay.draw(graphics, x, y, width, height, style);
		}
	}
	
}
