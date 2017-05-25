package com.prism.model;

import com.prism.model.drawn.ColorDrawable;
import com.prism.model.drawn.ColorGradientDrawable;
import com.prism.model.drawn.ColorGradientDrawable.Style;

public class Color {
	
	public static final ColorDrawable RED() {
		return parse(255, 0, 0, 255);
	}
	
	public static final ColorDrawable GREEN() {
		return parse(0, 255, 0, 255);
	}
	
	public static final ColorDrawable BLUE() {
		return parse(0, 0, 255, 255);
	}
	
	public static final ColorDrawable ALPHA() {
		return parse(0, 0, 0, 0);
	}
	
	public static ColorDrawable parse(int r, int g, int b, int a) {
		return new ColorDrawable(new java.awt.Color(r, g, b, a));
	}
	
	public static ColorGradientDrawable parseg(int r, int g, int b, int a) {
		return new ColorGradientDrawable(new java.awt.Color(r, g, b, a), Style.TOP_DOWN);
	}
	
	public static ColorDrawable parse(int r, int g, int b) {
		return new ColorDrawable(new java.awt.Color(r, g, b));
	}
	
	public static ColorGradientDrawable parseg(int r, int g, int b) {
		return new ColorGradientDrawable(new java.awt.Color(r, g, b), Style.TOP_DOWN);
	}
	
	public static ColorDrawable parse(String color) {
		if(color.charAt(0) != '#') {
			color = "#" + color;
		}
		return new ColorDrawable(java.awt.Color.decode(color));
	}
	
	public static ColorGradientDrawable parseg(String color) {
		if(color.charAt(0) != '#') {
			color = "#" + color;
		}
		return new ColorGradientDrawable(java.awt.Color.decode(color), Style.TOP_DOWN);
	}
	
	public static ColorGradientDrawable parseg(String alpha, String beta) {
		return parseg(alpha, beta, Style.TOP_DOWN);
	}
	
	public static ColorGradientDrawable parseg(String alpha, String beta, Style style) {
		if(alpha.charAt(0) != '#') {
			alpha = "#" + alpha;
		}
		if(beta.charAt(0) != '#') {
			beta = "#" + beta;
		}
		return new ColorGradientDrawable(java.awt.Color.decode(alpha), java.awt.Color.decode(beta), style);
	}
	
	public static int toInt(String color) {
		if(color.charAt(0) != '#') {
			color = "#" + color;
		}
		return java.awt.Color.decode(color).getRGB();
	}

	public static java.awt.Color color(String color) {
		return java.awt.Color.decode(color);
	}

	public static ColorDrawable random() {
		return parse((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random()));
	}
	
	public static ColorGradientDrawable randomg() {
		return parseg((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random()));
	}

}
