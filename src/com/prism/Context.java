package com.prism;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Context {
	
	private final HashMap<String, Object> attributes;
	
	public static final Class<? extends float[]> FLOAT_GENERIC = new float[0].getClass();
	
	public Context() {
		attributes = new HashMap<String, Object>();
	}
	
	public void remove(String key) {
		attributes.remove(key);
	}
	
	public void bridge(Context other) {
		for(String set : other.attributes.keySet()) {
			attributes.put(set, other.attributes.get(set));
		}
	}
	
	public Object set(String key, Object value) {
		return attributes.put(key, value);
	}
	
	public <T> T $(String key, Class<T> type) {
		return $(key, type, null);
	}
	
	public <T> T $(String key, Class<T> type, T defaulted) {
		Object object = attributes.get(key);
		if(object != null) {
			return type.cast(object);
		}
		return defaulted;
	}
	
	public boolean is(String key) {
		return $(key, Boolean.class, false);
	}
	
	public boolean is(String key, boolean defaulted) {
		return $(key, Boolean.class, defaulted);
	}
	
	public boolean is(String key, Object value) {
		if(exist(key)) {
			return attributes.get(key).equals(value);
		}
		return false;
	}

	public boolean exist(String string) {
		return attributes.containsKey(string);
	}

	public String $(String string) {
		return $(string, String.class, null);
	}

	public Object raw(String name) {
		return attributes.get(name);
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		return attributes.entrySet();
	}

}
