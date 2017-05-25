package com.prism.model.components;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.prism.model.Bounds;
import com.prism.model.Component;
import com.prism.model.Text;
import com.prism.model.Thing.Action;

public class Label extends Component implements Action {

	private static final String STYLE = "label";

	public static final String WORD_WRAP = "word_wrap";
	public static final String FONT   = "font";
	public static final String SIZE   = "font_size";
	public static final String COLOR  = "font_color";
	public static final String ALIGN  =  "align";
	public static final String V_ALIGN = "valign";

	private Text text;
	private Rectangle2D container;
	private Font font;
	private boolean bounded;
	private Bounds.Mold bounds;
	private Bounds.Mold textBounds;
	private ArrayList<Snippet> division;

	private int uWidth;
	private int uHeight;

	public enum Align {
		START,
		END,
		MIDDLE;
	}

	public Label(Bounds bounds, Text text) {
		super(bounds, STYLE);
		this.text = text;
		this.bounded = bounds != null;
		this.textBounds = new Bounds.Mold(0F, 0F);
		if(!bounded) {
			this.bounds = new Bounds.Mold(0F, 0F);
			super.bound(this.bounds);
		}
		this.division = new ArrayList<Snippet>();
		text.setOnDirtListener(this);
	}

	@Override
	public void bound(Bounds bounds) {
		super.bound(bounds);
		this.bounded = bounds != null;
		update();
	}

	public Label(Bounds bounds, String text) {
		this(bounds, new Text(text));
	}

	public Label(Text text) {
		this(null, text);
	}


	public Label(String text) {
		this(null, text);
	}

	@Override
	public void state(int state) {
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		graphics.setFont(font);
		graphics.setColor(style.color(COLOR));
		for(Snippet snippet : division) {
			graphics.drawString(snippet.text, snippet.x, snippet.y);
		}
		if(uWidth != width || uHeight != height || text.isDirty()) {
			update();
		}
	}

	@Override
	public void update(int width, int height, Graphics2D graphics) {
		uWidth  = width;
		uHeight = height;
		Align align  = style.align(ALIGN);
		Align valign = style.align(V_ALIGN);
		font = style.font(FONT);
		font = font.deriveFont(style.number(SIZE));
		graphics.setFont(font);
		division.clear();
		container = graphics.getFontMetrics().getStringBounds(text.getString(), graphics);
		if(!bounded) {
			bounds.setWidth ((float) container.getWidth());
			bounds.setHeight((float) container.getHeight());

			textBounds.setWidth ((float) container.getWidth());
			textBounds.setHeight((float) container.getHeight());

			Snippet snippet = new Snippet();
			snippet.text = text.getString();
			snippet.y = (int) -container.getY();
			division.add(snippet);

		} else if(style.is(WORD_WRAP)) {
			String resolve = text.getString();
			FontMetrics metrics = graphics.getFontMetrics();
			Rectangle2D average = metrics.getStringBounds(resolve, graphics);
			int x = 0;
			int y = (int) -average.getY();
			int limit = (int) width();
			char[] characters = resolve.toCharArray();
			String raw = "";
			for(int i = 0; i < characters.length; i++) {
				Rectangle2D bounds = metrics.getStringBounds(raw + characters[i], graphics);
				if((""+characters[i]).contains("\n") || bounds.getWidth() > limit) {
					Rectangle2D sb = metrics.getStringBounds(raw, graphics);
					Snippet snippet = new Snippet();
					snippet.width = (int) sb.getWidth();
					snippet.height = (int) sb.getHeight();
					snippet.text = raw;
					division.add(snippet);
					raw = "" + characters[i];
				} else {
					raw += characters[i];
				}
			}
			Rectangle2D sb = metrics.getStringBounds(raw, graphics);
			Snippet snippet = new Snippet();
			snippet.width = (int) sb.getWidth();
			snippet.height = (int) sb.getHeight();
			snippet.text = raw;
			division.add(snippet);
			int th = 0;
			int tw = 0;
			for(Snippet snipet : division) {
				if(align.equals(Align.MIDDLE)) {
					x = (int) (-snipet.width/2 + width()/2);
				} else if(align.equals(Align.END)) {
					x = (int) (width() - snipet.width);
				}
				snipet.x = x;
				snipet.y = y;
				tw = Math.max(tw, width);
				y  += snipet.height;
				th += snipet.height;
			}
			textBounds.setHeight(th);
			textBounds.setWidth(tw);
		} else {
			Snippet snippet = new Snippet();
			snippet.text = text.getString();
			float x = 0F;
			if(align.equals(Align.MIDDLE))
				x = (float) (width/2F - container.getWidth()/2F);
			else if(align.equals(Align.END))
				x = (int) (width() - container.getWidth());
			snippet.x = (int) x;
			snippet.y = (int) -container.getY();
			if(valign.equals(Align.MIDDLE)) {
				snippet.y += (height()/2 - container.getHeight()/2) + 2.5F;
			}
			division.add(snippet);
			textBounds.setWidth ((float) container.getWidth());
			textBounds.setHeight((float) container.getHeight());
		}
		text.clean();
	}

	@Override
	public boolean mouseEvent(float x, float y, int type, boolean control,
			boolean shift) {
		return false;
	}

	@Override
	public void act(Object... elements) {
		update();
	}

	public Bounds.Mold getTextBounds() {
		return textBounds;
	}

	public Text getText() {
		return text;
	}

	private static class Snippet {
		public String text;
		public int width;
		public int height;
		public int x;
		public int y;
	}

	@Override
	public void overlay(Graphics2D graphics, int width, int height) {
	}

	@Override
	public boolean keyEvent(int key, boolean release, String text,
			boolean control, boolean shift) {
		return false;
	}

	@Override
	public int getMouseCursor(float x, float y) {
		return -1;
	}

	public void setText(Text text) {
		this.text = text;
		this.text.setOnDirtListener(this);
		update();
	}


	@Override
	public boolean dragged(int x, int y) {
		return false;
	}

	@Override
	public void hover(float x, float y) {

	}


}
