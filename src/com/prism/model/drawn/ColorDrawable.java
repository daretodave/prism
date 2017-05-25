package com.prism.model.drawn;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

import com.prism.Context;
import com.prism.model.Color;
import com.prism.model.Drawable;

public class ColorDrawable extends Drawable {
	
	@Override
	public String toString() {
		return "ColorDrawable [color=" + color + ", integer=" + integer + "] ";
	}

	private java.awt.Color color;
	private int integer;
	
	public ColorDrawable(java.awt.Color color) {
		this.color = color;
		this.integer = color.getRGB();
	}
	
	public ColorDrawable(int color) {
		this(new java.awt.Color(color, true));
	}

	public ColorDrawable(String string) {
		this(java.awt.Color.decode(string));
	}

	@Override
	public void draw(Graphics2D graphics, int x, int y, int width, int height, Context context) {
		graphics.setColor(color);
		if(context.is(DISPLAY_INNER, true)) {
			if(context.exist(RADIUS)) {
				float radius = context.$(RADIUS, Float.class);
				graphics.fillRoundRect(x, y, width, height, (int)radius, (int)radius);
			} else {
				graphics.fillRect(x, y, width, height);
			}
		}
		if(context.exist(BORDER)) {
			float border = context.$(BORDER, Float.class);
			java.awt.Color bcolor = color.darker();
			if(context.exist(BORDER_COLOR)) {
				Object o = context.raw(BORDER_COLOR);
				if(o instanceof String) {
					bcolor = java.awt.Color.decode(o.toString());
				} else if(o instanceof ColorDrawable) {
					bcolor = ((ColorDrawable) o).getColor();
				}
			}
			Stroke prior = graphics.getStroke();
			graphics.setStroke(new BasicStroke(border));
			graphics.setColor(bcolor);
			if(context.exist(RADIUS)) {
				float radius = context.$(RADIUS, Float.class);
				graphics.drawRoundRect(x, y, width, height, (int)radius, (int)radius);
			} else {
				graphics.drawRect(x, y, width, height);
			}
			graphics.setStroke(prior);
		}
	}
	
	public void setColor(int color) {
		this.color = new java.awt.Color(color, true);
		this.integer = color;
	}
	
	public void setColor(java.awt.Color color) {
		this.color = color;
		this.integer = color.getRGB();
	}
	
	public int getInteger() {
		return integer;
	}
	
	public int getRed() {
		return color.getRed();
	}
	
	public int getGreen() {
		return color.getGreen();
	}
	
	public int getBlue() {
		return color.getBlue();
	}
	
	public ColorDrawable darker() {
		return new ColorDrawable(color.darker());
	}

	public ColorDrawable brighter() {
		return new ColorDrawable(color.brighter());
	}
	
	public ColorDrawable alpha(int alpha) {
		int rgb = getInteger();
		int red   = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue  = rgb & 0xFF;
		return Color.parse(red, green, blue, alpha);
	}

	@Override
	public Drawable copy() {
		return new ColorDrawable(color);
	}

	public java.awt.Color getColor() {
		return color;
	}
}
