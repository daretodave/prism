package com.prism.model.components;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import com.prism.cache.Cache;
import com.prism.model.Bounds;
import com.prism.model.Component;

public class Image extends Component {
	
	private BufferedImage image;
	private String folder;
	private String asset;
	private Scale scale;
	
	public enum Scale {
		CENTER,
		CENTER_CROP,
		FIT;
	}

	public Image(Bounds bounds, String folder, String asset, Scale scale) {
		super(bounds, BLANK_STYLE);
		this.folder = folder;
		this.asset  = asset;
		this.scale  = scale;
	}
	
	public Image(Bounds bounds, String asset, Scale scale) {
		this(bounds, null, asset, scale);
	}
	
	public Image(Bounds bounds, String asset) {
		this(bounds, asset, Scale.FIT);
	}
	
	public void setImage(String folder, String asset) {
		this.asset  = asset;
		this.folder = folder;
		update();
	}

	@Override
	public int getMouseCursor(float x, float y) {
		return -1;
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		int iwidth  	   = image.getWidth();
		int iheight 	   = image.getHeight();
		Shape prior = graphics.getClip();
		graphics.clipRect(0, 0, width, height);
		switch(scale) {
		case CENTER:
			if(width > height) {
				float ratio = (float)iwidth/(float)iheight;
				int w = (int) (width*ratio);
				int h = (int) ((float)w/ratio);
				graphics.drawImage(image, 0, height/2-h/2, w, h, null);
			} else {
				float ratio = (float)iheight/(float)iwidth;
				int h = (int) (height*ratio);
				int w = (int) ((float)h/ratio);
				graphics.drawImage(image, width/2-w/2, 0, w, h, null);
			}
			break;
		case CENTER_CROP:
			if(iwidth > iheight) {
				float ratio = (float)iwidth/(float)iheight;
				int w = (int) (width*ratio);
				int h = (int) ((float)w/ratio);
				graphics.drawImage(image, 0, height/2-h/2, w, h, null);
			} else {
				float ratio = (float)iheight/(float)iwidth;
				int h = (int) (height*ratio);
				int w = (int) ((float)h/ratio);
				graphics.drawImage(image, width/2-w/2, 0, w, h, null);
			}
			break;
		case FIT:
			graphics.drawImage(image, 0, 0, width, height, null);
			break;
		}
		graphics.setClip(prior);
	}

	@Override
	public void overlay(Graphics2D graphics, int width, int height) {		
	}

	@Override
	public void update(int width, int height, Graphics2D graphics) {
		if(folder != null)
			image = Cache.image(asset, folder);
		else 
			image = Cache.asset(asset, BufferedImage.class);
	}

	@Override
	public boolean mouseEvent(float x, float y, int type, boolean control,
			boolean shift) {
		return false;
	}

	@Override
	public boolean keyEvent(int key, boolean release, String text,
			boolean control, boolean shift) {
		return false;
	}

	@Override
	public void state(int state) {		
	}

	public Scale getScale() {
		return scale;
	}

	public void setScale(Scale scale) {
		this.scale = scale;
	}
	

	@Override
	public boolean dragged(int x, int y) {
		return false;
	}
	
	@Override
	public void hover(float x, float y) {
		
	}


}
