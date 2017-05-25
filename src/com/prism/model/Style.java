package com.prism.model;

import java.awt.Color;
import java.awt.Font;
import java.util.Map.Entry;

import com.prism.Context;
import com.prism.cache.Asset;
import com.prism.cache.Cache;
import com.prism.model.components.Label.Align;
import com.prism.model.drawn.ColorDrawable;
import com.prism.model.drawn.ColorGradientDrawable;

public class Style {
	
	private Context context;
	private String name;
	
	public Style(String name) {
		this.name = name;
		this.context = new Context();
	}
	
	public String getName() {
		return name;
	}
	
	public void set(String key, Object value) {
		context.set(key, value);
	}
	
	public Drawable drawable(String key) {
		return (Drawable) context.raw(key);
	}
	
	public Align align(String key) {
		return (Align) context.raw(key);
	}
	
	public boolean is(String key) {
		return context.is(key);
	}
	
	@SuppressWarnings("unchecked")
	public Font font(String key) {
		Object o = context.raw(key);
		if(o instanceof String) {
			Font font = Cache.asset(o.toString(), Font.class);
			set(key, font);
			return font;
		} else if(o instanceof Font) {
			return (Font) o;
		} else if(o instanceof Asset) {
			return ((Asset<Font>) o).getAsset();
		}
		throw new RuntimeException("Asset ["+key+"] does not exist in this style.. dying.");
	}
	
	public float number(String key) {
		return (Float) context.raw(key);
	}
	
	@SuppressWarnings("unchecked")
	public java.awt.Color color(String key) {
		Object o = context.raw(key);
		if(o instanceof ColorDrawable) {
			return ((ColorDrawable) o).getColor();
		} else if(o instanceof ColorGradientDrawable) {
			return ((ColorGradientDrawable) o).getColor();
	    } else if(o instanceof String) {
			java.awt.Color color = java.awt.Color.decode(o.toString()); 
			context.set(key, color); //would be costly create a new color every time?
			return color;
		} else if(o instanceof Color) {
			return (Color) o;
		} else if(o instanceof Asset) {
			return  ((Asset<Color>) o).getAsset();
		}
		throw new RuntimeException("Asset ["+key+"] does not exist in this style.. dying.");
	}

	public void bridge(Style style) {
		for(Entry<String, Object> entry : style.context.entrySet()) {
			context.set(entry.getKey(), entry.getValue());
		}
	}


}
