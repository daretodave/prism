package com.prism.cache.impl;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import com.prism.cache.Loader;
import com.prism.cache.StreamUtils;
import com.prism.model.Style;
import com.prism.model.components.Label.Align;
import com.prism.model.drawn.ColorDrawable;
import com.prism.model.drawn.ColorGradientDrawable;

public class StyleLoader implements Loader<Style> {
	
	public static HashMap<String, Class<?>> entities;
	
	static {
		entities = new HashMap<String, Class<?>>();
		entities.put("C",  ColorDrawable.class);
		entities.put("CG", ColorGradientDrawable.class);
	}

	@Override
	public Style load(InputStream input, String title) throws Exception {
		String file = StreamUtils.sap(input, "#", "//");
		Style style = new Style(title);
		HashMap<String, String> values = new HashMap<String, String>();
		for(int i = 0; i < file.length(); i++) {
			char c = file.charAt(i);
			String prestring = file.substring(0, i);
			String substring = file.substring(i);
			if(c == '=') {
				int prior = prestring.lastIndexOf(";");
				int previous = prior == -1 ? 0 : prior+1;
				String key = prestring.substring(previous, i-1).trim();
				int next = substring.indexOf(";");
				String value = substring.substring(1, next).trim();
				values.put(key,  value);
			}
		}
		for(Entry<String, String> entry : values.entrySet()) {
			String key   = entry.getKey();
			String value = entry.getValue();
			int index = value.indexOf("(");
			if(index == -1) {
				style.set(key, parse(value));
			} else {
				String param = value.substring(index+1, value.length()-1);
				String clazz = value.substring(0, index);
				Class<?> element = entities.get(clazz);
				if(element == null) {
					throw new RuntimeException("Object->"+clazz+" does not exist. Use 'PSLoader.entities' to build objects.");
				}
				int section = param.indexOf("[");
				String arguments = param;
				String spec = "";
				if(section != -1) {
					arguments = arguments.substring(0, section).trim();
					if(arguments.charAt(arguments.length()-1) == ',')
						arguments = arguments.substring(0, arguments.length()-1);
					spec = param.substring(section+1, param.length()-1).trim();
				}
				Object build = create(element, arguments, spec);
				apply(build, spec);
				style.set(key, build);
			}
			
		}
		return style;
	}

	private void apply(Object build, String spec) throws Exception {
		if(spec == null || spec.isEmpty()) {
			return;
		}
		Method applied = build.getClass().getMethod("set", String.class, Object.class);
		if(applied == null) {
			throw new RuntimeException(build.getClass().getName() + " does not have a set method with a string-object parameter.");
		}
		String[] division = spec.split(",");
		for(int i = 0; i < division.length; i++) {
			String expression  = division[i];
			String[] expressed = expression.split("~");
			String key   = expressed[0].trim();
			Object value = parse(expressed[1].trim());
			applied.invoke(build, key, value);
		}
	}

	private <T> T create(Class<T> element, String arguments, String spec) throws Exception {
		Object[] args = arguments(arguments);
		if(args.length == 0) {
			return element.newInstance();
		}
		Class<?>[] translated = translate(args);
		Constructor<T> constructor = element.getConstructor(translated);
		return constructor.newInstance(args);
	}

	private Class<?>[] translate(Object[] args) {
		Class<?>[] translation = new Class<?>[args.length];
		for(int i = 0; i < args.length; i++) {
			translation[i] = args[i].getClass();
		}
		return translation;
	}

	private Object[] arguments(String arguments) {
		if(arguments == null || arguments.isEmpty())
			return new Object[0];
		String[] division = arguments.split(",");
		Object[] elements = new Object[division.length];
		for(int i = 0; i < elements.length; i++) {
			elements[i] = parse(division[i]);
		}		
		return elements;
	}

	private Object parse(String string) {
		string = string.trim();
		if(string.toLowerCase().startsWith("align.")) {
			return Align.valueOf(string.split("\\.")[1].toUpperCase());
		}
		if(string.startsWith("#")) {
			return string;
		}
		if(string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false")) {
			return Boolean.parseBoolean(string);
		}
		try {
			return Float.parseFloat(string);
		} catch(Exception e) {
			return string;
		}
	}
	


}
