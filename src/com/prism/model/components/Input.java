package com.prism.model.components;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.prism.Prism;
import com.prism.model.Bounds;
import com.prism.model.Component;
import com.prism.model.Drawable;
import com.prism.model.Location.Hook;
import com.prism.model.Text;
import com.prism.model.Value;
import com.prism.model.components.Label.Align;

public class Input extends Component implements Value {
	
	public static final int NOT_EDITABLE 	   	     = 1;
	public static final int PASSWORD     			 = 2;
	public static final int CAPITALIZE_EVERYTHING    = 4;
	public static final int CAPITALIZE_FIRST_LETTERS = 8;
	public static final int LOWERCASE_EVERYTHING     = 16;
	public static final int NOT_PASTABLE             = 32;
	
	public static final int CHANGE_TYPED  = 1;
	public static final int CHANGE_PASTED = 2;
	
	private static final String STYLE      = "input";
	private static final String TEXT_STYLE = "input_text";
	private static final String TEXT_HINT_STYLE = "input_hint";
	
	private Drawable background;
	private Drawable forground;
	private float padding;
	
	public final Label text;
	public final Label hint;
		
	public Input(Bounds bounds, Align align) {
		super(bounds, STYLE);
		text = new Label(Bounds.implode(bounds, this), new Text());
		text.clip(this);
		text.style(TEXT_STYLE);
		text.locate(this, Hook.C);
		
		hint = new Label(text.bounding(), new Text());
		hint.clip(this);
		hint.style(TEXT_STYLE);
		hint.style(TEXT_HINT_STYLE);
		hint.locate(this, Hook.C);
		
		attach(text);
		attach(hint);
	}
	
	@Override
	public void enable(int flag) {
		if(flag == PASSWORD) {
			text.getText().setMasked(true);
			text.update();
		}
		super.enable(flag);
	}
	
	@Override
	public void disable(int flag) {
		if(flag == PASSWORD) {
			text.getText().setMasked(true);
			text.update();
		}
		super.disable(flag);
	}
	
	
	public void hint(String text) {
		hint.getText().update(text);
	}

	public Input(Bounds bounds) {
		this(bounds, Align.START);
	}

	@Override
	public void state(int state) {
		update();
	}
	
	@Override
	public void bound(Bounds bounds) {
		super.bound(bounds);
		text.bound(Bounds.implode(bounds, this));
		hint.bound(Bounds.implode(bounds, this));
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		background.render(graphics, width, height);
	}

	@Override
	public void overlay(Graphics2D graphics, int width, int height) {
		forground.render(graphics, width, height);
	}

	@Override
	public void update(int width, int height, Graphics2D graphics) {
		boolean focused = getContext().is(FOCUSED);
		background = style.drawable(BACKGROUND);
		forground  = style.drawable(focused ? FORGROUND_FOCUSED : FORGROUND);
		padding    = style.number(PADDING);
		hint.hide(focused || !text().isEmpty());
	}

	@Override
	public boolean mouseEvent(float x, float y, int type, boolean control,
			boolean shift) {
		return true;
	}

	@Override
	public boolean keyEvent(int key, boolean release, String text,
			boolean control, boolean shift) {
		if(is(NOT_EDITABLE)) {
			return true;
		}
		if(control) {
			if(!release && key == KeyEvent.VK_V && !is(Input.NOT_PASTABLE)) {
				String clipboard = Prism.getClipboardContents();
				if(clipboard != null && !clipboard.isEmpty()) {
					append(clipboard);
					change(CHANGE_PASTED);
				}
			}
			return true;
		}
		if(!release) {
			switch(key) {
			case KeyEvent.VK_BACK_SPACE:
				this.text.getText().erase();
				change(CHANGE_TYPED);
				break;
			default:
				if(text != null) {
					append(text);
					change(CHANGE_TYPED);
				}
				break;
			}
		}
		return true;
	}

	private void append(String text) {
		String prior = text();
		if(is(CAPITALIZE_FIRST_LETTERS) && (prior.isEmpty() || (!prior.isEmpty() && text != " " && prior.charAt(prior.length()-1) == ' '))) {
			text = text.toUpperCase();
		}
		if(is(CAPITALIZE_EVERYTHING)) {
			text = text.toUpperCase();
		}
		if(is(LOWERCASE_EVERYTHING)) {
			text = text.toLowerCase();
		}
		this.text.getText().append(text);
	}

	@Override
	public int getMouseCursor(float x, float y) {
		return Cursor.TEXT_CURSOR;
	}
	
	public String text() {
		return text.getText().getTrueString();
	}

	@Override
	public float getValue() {
		return padding;
	}

	@Override
	public boolean dragged(int x, int y) {
		return false;
	}
	@Override
	public void hover(float x, float y) {
		
	}


}
