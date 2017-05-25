package com.prism.model.components;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.prism.model.Bounds;
import com.prism.model.Component;
import com.prism.model.Drawable;
import com.prism.model.Location.Hook;
import com.prism.model.Region;
import com.prism.model.Text;
import com.prism.model.Thing;
import com.prism.model.components.Image.Scale;

public class Button extends Component {
	
	private static final String STYLE = "button";
	private static final String STYLE_TEXT = "button_text";
	
	private static final float DEFAULT_SEPERATION = 7F;
	
	private Drawable background;
	private Region region;
	private float distance;
	
	public Label label;
	public Image image;
	
	
	public static final int CLICK = 0x0;
	public static final int ENTER = 0x1;
	
	public Button(Bounds bounds, String text) {
		this(bounds, new Text(text), null, DEFAULT_SEPERATION);
	}
	
	public Button(Bounds bounds, String text, String asset, String folder, float thumbnail) {
		this(bounds, text == null ? null : new Text(text), new Image(new Bounds.Mold(thumbnail, thumbnail), folder, asset, Scale.CENTER_CROP), DEFAULT_SEPERATION);
	}
	
	public Button(Bounds bounds, String text, String asset, float thumbnail, float seperation) {
		this(bounds, text == null ? null : new Text(text), new Image(new Bounds.Mold(thumbnail, thumbnail), null, asset, Scale.CENTER_CROP), seperation);
	}
	
	public Button(Bounds bounds, String asset, String folder, float thumbnail) {
		this(bounds, null, asset, folder, thumbnail);
	}
	
	public Button(Bounds bounds, String asset, float thumbnail) {
		this(bounds, asset, null, thumbnail);
	}

	public Button(Bounds bounds, Text text, Image icon, float seperation) {
		super(bounds, STYLE);
		context.set(NOT_FOCUSABLE_FROM_CLICK, true);
		this.distance = seperation;
		region = new Region(new Bounds() {
			@Override
			public float getWidth() {
				float width = 0F;
				if(image != null) {
					width += image.width();
					
					if(label != null) {
						width += distance;
					}
				}
				if(label != null) {
					width += label.getTextBounds().width();
				}
				return width;
			}
			@Override
			public float getHeight() {
				if(label != null && image != null) {
					return Math.max(label.getTextBounds().height(), image.height());
				} else if(label != null) {
					return label.getTextBounds().height();
				} else if(image != null) {
					return image.height();
				}
				return 0;
			}
		});
		region.locate(this, Hook.C);
		attach(region);
		if(icon != null) {
			setImage(icon);
		}
		if(text != null) {
			setText(text);
		}
	}
	
	public void setImage(Image image) {
		if(this.image != null) {
			detach(this.image);
		}
		if(image != null) {
			image.locate(region, Hook.CW);
			attach(image);
			this.image = image;
		}
	}
	
	public void setLabel(Label label) {
		if(this.label != null) {
			detach(this.label);
		}
		if(label != null) {
			label.locate(region, Hook.CE);
			attach(label);
			this.label = label; 
		}
	}
	
	public void setText(Text text) {
		if(label == null) {
			label = new Label(text);
			label.style(STYLE_TEXT);
			setLabel(label);
		} else {
			label.setText(text);
		}
	}
	
	@Override
	public void bound(Bounds bounds) {
		super.bound(bounds);
	}

	@Override
	public int getMouseCursor(float x, float y) {
		return Cursor.HAND_CURSOR;
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		background.render(graphics, width, height);
	}

	@Override
	public void overlay(Graphics2D graphics, int width, int height) {
		
	}

	@Override
	public void update(int width, int height, Graphics2D graphics) {
		background = style.drawable((context.is(FOCUSED) || context.is(PRESSED) ? BACKGROUND_FOCUSED : (context.is(HOVERED) ?  BACKGROUND_HOVERED : BACKGROUND)));
	}

	@Override
	public boolean mouseEvent(float x, float y, int type, boolean control,
			boolean shift) {
		return true;
	}

	@Override
	public boolean keyEvent(int key, boolean release, String text,
			boolean control, boolean shift) {
		if(release && key == KeyEvent.VK_ENTER) {
			action(ENTER);
		}
		return false;
	}

	@Override
	public void state(int state) {
		update();
		if(state == Thing.RELEASE_IN_BOUNDS) {
			action(CLICK);
		}
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}


	@Override
	public boolean dragged(int x, int y) {
		return false;
	}

	@Override
	public void hover(float x, float y) {
		
	}

}
