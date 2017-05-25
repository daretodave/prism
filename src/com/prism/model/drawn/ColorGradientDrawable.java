package com.prism.model.drawn;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

import com.prism.Context;
import com.prism.model.Drawable;

public class ColorGradientDrawable extends Drawable {
	
	@Override
	public String toString() {
		return "ColorGradientDrawable [alpha=" + alpha + ", beta=" + beta
				+ ", style=" + style + "]";
	}

	private java.awt.Color alpha;
	private java.awt.Color beta;
	private int pHeight;
	private int pWidth;
	private Paint paint;
	private Style style;
	
	public enum Style {
		TOP_DOWN
	}
	
	public ColorGradientDrawable(Color alpha, Color beta, Style style) {
		this.alpha = alpha;
		this.beta  = beta;
		this.style = style;
	}
	
	public ColorGradientDrawable(Color alphabeta, Style style) {
		this(alphabeta, alphabeta.darker(), style);
	}
	
	public ColorGradientDrawable(String alpha, String beta, Style style) {
		this(java.awt.Color.decode(alpha), java.awt.Color.decode(beta), style);
	}
	
	public ColorGradientDrawable(String alpha, String beta) {
		this(alpha, beta, Style.TOP_DOWN);
	}
	
	public ColorGradientDrawable(String alphabeta, Style style) {
		this(java.awt.Color.decode(alphabeta), style);
	}
	
	public ColorGradientDrawable(String alphabeta) {
		this(alphabeta, Style.TOP_DOWN);
	}

	@Override
	public void draw(Graphics2D graphics, int x, int y, int width, int height, Context context) {
		if(pWidth != width || pHeight != height || paint == null) {
			pWidth  = width;
			pHeight = height;
			switch(style) {
			case TOP_DOWN:
				paint = new GradientPaint(x, y, alpha, x, height, beta, true);
				break;
			}
		}
		graphics.setPaint(paint);
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
			
			java.awt.Color bcolor = beta.darker();
			if(context.exist(BORDER_COLOR)) {
				bcolor = java.awt.Color.decode(context.$(BORDER_COLOR));
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

	@Override
	public Drawable copy() {
		return new ColorGradientDrawable(alpha, beta, style);
	}

	public Color getColor() {
		return alpha;
	}

}
