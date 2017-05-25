package com.prism.model;

import com.prism.model.Thing.Action;

public class Text {
	
	private String text;
	private boolean dirty;
	private Action onDirtListener;
	private boolean masked;
	
	public Text(String text) {
		this.text  = text;
		this.setDirty(true);
	}
	
	public Text() {
		this("");
	}

	public void update(String text) {
		this.text  = text;
		this.setDirty(true);
	}
	
	public void clean() {
		setDirty(false);
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public String getString() {
		if(masked) {
			String masked = "";
			for(int i = 0; i < text.length(); i++) {
				masked += "\u2022 ";
			}
			return masked;
		}
		return text;
	}
	
	public String getTrueString() {
		return text;
	}
	
	public void append(String text) {
		this.text  = this.text + text;
		setDirty(true);
	}
	
	public void erase() {
		if(text.length() > 0)
			this.text = text.substring(0, text.length()-1);
		this.setDirty(true);
	}
	
	public void clear() {
		this.text = "";
		setDirty(true);
	}
	
	public int length() {
		return text.length();
	}

	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		if(dirty && onDirtListener != null) {
			onDirtListener.act(this);
		}
	}

	public void setOnDirtListener(Action onCleanListener) {
		this.onDirtListener = onCleanListener;
	}

	public boolean isMasked() {
		return masked;
	}

	public void setMasked(boolean masked) {
		this.masked = masked;
	}

}
