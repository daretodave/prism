package com.prism;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import com.prism.logic.Input;
import com.prism.logic.Key;
import com.prism.logic.Renderer;
import com.prism.model.Bounds;
import com.prism.model.Location;
import com.prism.model.Location.Hook;
import com.prism.model.Region;
import com.prism.model.ResizePostulate;
import com.prism.model.Thing;
import com.prism.model.ThingList;
import com.prism.model.Value;
import com.prism.model.Value.StaticValue;

@SuppressWarnings("serial")
public class Window extends JFrame implements Runnable, KeyListener, MouseListener, Thing, MouseMotionListener, ComponentListener {
	
	public static final String TITLE  	      = "__TITLE";
	public static final String WIDTH  	      = "__WIDTH";
	public static final String HEIGHT 		  = "__HEIGHT";
	public static final String TAG    		  = "__TAG";
	public static final String BACKGROUND     = "__BACKGROUND";
	public static final String MASTER_CONTROL = "__MASTER_CONTROL";
	public static final String DECORATION     = "__DECORATION";
	public static final String DRAGABLE       = "__DRAGABLE";
	public static final String TOP_ALWAYS     = "__TOP_ALWAYS";
	public static final String RESIZABLE 	  = "__RESIZABLE";
	public static final String ICON		 	  = "__ICON";
	
	public static final String RETAIN_X    = "__RETAIN_X";
	public static final String RETAIN_Y    = "__RETAIN_Y";
	public static final String RETAIN_W    = "__RETAIN_W";
	public static final String RETAIN_H    = "__RETAIN_H";
	public static final String KEY_MAP     = "__KEYMAP__";

	private boolean resizing;
	
	private Region[] resize;
	
	public enum Decoration {
		DECORATED,
		UNDECORATED,
		PRISM;
	}
	
	private Renderer renderer;
	private boolean running;
	private boolean open;
	private BufferStrategy strategy;
	private Input input;
	private Context context;
	private ThingList<Thing> children;
	private Location location;
	private Bounds bounds;
	private Canvas canvas;
	private Region toolbar;

	public Window(String title, int width, int height, Renderer renderer, Input input) {
		super(title);
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFocusTraversalKeysEnabled(false);
		this.input = input;
		this.renderer = renderer;
		this.location = new Location.Mold(getX(), getY());
		this.bounds   = new Bounds.Mold(width, height);
		this.children = new ThingList<Thing>(this);
		canvas = new Canvas();
		canvas.setBackground(new java.awt.Color(0, 0, 0, 0));
		canvas.setFocusTraversalKeysEnabled(false);
		add(canvas);
	}

	@Override
	public void run() {
		running = true;
		while(running) {
			if(isUndecorated()) {
				if((int)getY() != (int)location.y() || (int)getX() != (int)location.x()) {
					setLocation((int)location.x(), (int)location.y());
				}
				if(getWidth() != bounds.width()) {
					setSize((int)bounds.width(), getHeight());
				}
				if(getHeight() != bounds.height()) {
					setSize(getWidth(), (int)bounds.height());
				}
			}
			Graphics2D g = null;
			try {
				g = (Graphics2D) strategy.getDrawGraphics();
				try {
					renderer.render(this, g, getWidth(), getHeight());
				} catch(Exception e){
					e.printStackTrace();
					System.exit(-1);
				}
			} finally {
				g.dispose();
			}
			strategy.show();
		}
		dispose();
	}
	
	public void hide(boolean b) {
		setVisible(!b);
	}
	
	public void close() {
		running = false;
		Prism.destroy(this);
	}
	
	public void setResize(Region[] regions) {
		this.resize = regions;
	}
	
	public void open() {
		if(!open) {
			setVisible(true);
			canvas.addKeyListener(this);
			canvas.addMouseMotionListener(this);
			canvas.addMouseListener(this);
			canvas.setIgnoreRepaint(true);
			canvas.addComponentListener(this);
			canvas.createBufferStrategy(2);
			strategy = canvas.getBufferStrategy();
			Prism.submit(this);
			open = true;
		}
	}
	
	public boolean isOpen() {
		return open;
	}


	@Override
	public void keyPressed(KeyEvent e) {
		Key key = Key.KEYS.get(e.getKeyCode());
		String text = null;
		if(key != null) {
			text = e.isShiftDown() ? key.getShiftOutput() : key.getOutput();
		}
		input.key(this, true, e.getKeyCode(), text, e.isControlDown(), e.isShiftDown());
	}


	@Override
	public void keyReleased(KeyEvent e) {
		//System.out.println("Hey " + e.getKeyCode());
		Key key = Key.KEYS.get(e.getKeyCode());
		String text = null;
		if(key != null) {
			text = e.isShiftDown() ? key.getShiftOutput() : key.getOutput();
		}
		input.key(this, false, e.getKeyCode(), text, e.isControlDown(), e.isShiftDown());
	}


	@Override
	public void keyTyped(KeyEvent e) {
	}


	@Override
	public void mouseClicked(MouseEvent e) {
	}


	@Override
	public void mouseEntered(MouseEvent e) {	
	}


	@Override
	public void mouseExited(MouseEvent e) {
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		input.mouse(this, false, e.getX(), e.getY(), e.isControlDown(), e.isShiftDown(), false);
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		input.mouse(this, true, e.getX(), e.getY(), e.isControlDown(), e.isShiftDown(), false);	
	}

	public Context getContext() {
		return context;
	}

	protected void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void attach(Thing thing) {
		synchronized(children) {
			children.add(thing);
		}
	}

	@Override
	public void detach(Thing thing) {
		synchronized(children) {
			children.remove(thing);
		}
	}

	@Override
	public ThingList<Thing> children() {
		
		return children;
	}

	@Override
	public float x() {
		return 0;
	}

	@Override
	public float y() {
		return 0;
	}

	@Override
	public float width() {
		return bounds.width();
	}

	@Override
	public float height() {
		return bounds.height();
	}

	@Override
	public void translate(float x, float y) {
		location.translate(x, y);
		
	}

	@Override
	public void grow(float w, float h) {
		float maxw = context.$(MAX_WIDTH , Value.class, Value.MAX).getValue();
		float maxh = context.$(MAX_HEIGHT, Value.class, Value.MAX).getValue();
		float minw = context.$(MIN_WIDTH , Value.class, StaticValue.CONSTANTS[10]).getValue();
		float minh = context.$(MIN_HEIGHT, Value.class, StaticValue.CONSTANTS[10]).getValue();
		if(w > 0 && bounds.width()+w > maxw) {
			w = maxw-bounds.width();
		} else if(w < 0 && bounds.width()+w < minw) {
			w = bounds.width()-minw;
		}
		if(h > 0 && bounds.height()+h > maxh) {
			h = maxh-bounds.height();
		} else if(h < 0 && bounds.height()+h < minh) {
			h = bounds.height()-minh;
		}
		bounds.translate(w, h);
	}

	@Override
	public void size(float width, float height) {
		if(bounds instanceof Bounds.Mold) {
			((Bounds.Mold) bounds).setWidth(width);
			((Bounds.Mold) bounds).setHeight(height);
		}
	}

	@Override
	public void moveto(float x, float y) {
		if(location instanceof Location.Mold) {
			((Location.Mold) location).setX(x);
			((Location.Mold) location).setY(y);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		input.drag(this, e.getX(), e.getY(), e.isControlDown(), e.isShiftDown());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		input.mouse(this, false, e.getX(), e.getY(), e.isControlDown(), e.isShiftDown(), true);
	}

	@Override
	public boolean mouse(float x, float y, int type, boolean ctrl, boolean shift, boolean hover) {
		ResizePostulate postulate = null;
		if(resize != null)
			for(Region region : resize) {
				if(region.contains(x, y)) {
					postulate = region.getContext().$(Prism.RESIZE_POSULTATE, ResizePostulate.class);
				}
			}
		if(hover && postulate != null) {
			context.set(Prism.RESIZE_POSULTATE, postulate);
			setCursor(postulate.getCursor());
		} else if(!hover && type == Thing.PRESS && postulate != null && !resizing) {
			resizing = true;
			context.set(Prism.RESIZE_POSULTATE, postulate);
		} else if(hover && postulate == null) {
			context.set(Prism.RESIZE_POSULTATE, null);
			resizing = false;
			int cursor = children.cursor(x, y);
			setCursor(cursor == -1 ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(cursor));
		}
		if(postulate != null) {
			return false;
		}
		return children.mouse(x, y, type, ctrl, shift, hover);
	}
	
	public boolean drag(int x, int y) {
		ResizePostulate postulate = context.$(Prism.RESIZE_POSULTATE, ResizePostulate.class);
		if(postulate != null) {
			int dx = x - context.$(Prism.PRESS_LOCATION_X, Integer.class);
			int dy = y - context.$(Prism.PRESS_LOCATION_Y, Integer.class);
			int dw = (int) (dx * postulate.getDw());
			int dh = (int) (dy * postulate.getDh());
			dx = (int) (postulate.getDx() != 0 ? dx*postulate.getDx() : 0);
			dy = (int) (postulate.getDy() != 0 ? dy*postulate.getDy() : 0);
			if(postulate.getDx() != 0 || postulate.getDy() != 0) {
				grow(dw, dh);
				translate(dx, dy);
				if(postulate.getDw() > 0) {
					context.set(Prism.PRESS_LOCATION_X, x);
				}
				if(postulate.getDh() > 0) {
					context.set(Prism.PRESS_LOCATION_Y, y);
				}
			} else {
				grow(dw, dh);
				context.set(Prism.PRESS_LOCATION_X, x);
				context.set(Prism.PRESS_LOCATION_Y, y);	
			}
			return false;
		}
		return !children.drag(x, y);
	}

	@Override
	public void keymap(int key, boolean release, Action action) {
		context.set(KEY_MAP+key+release, action);
	}

	@Override
	public boolean key(int key, boolean release, String text, boolean ctrl, boolean shift) {
		Action action = context.$(KEY_MAP+key+release, Action.class);
		if(action != null) {
			action.act(key, release, text, ctrl, shift);
			return true;
		}
		return children.key(key, release, text, ctrl, shift);
	}

	@Override
	public void locate(Thing other, Hook where) {
		this.location = new Location.Relative(this, other, where, where);	
	}

	@Override
	public void locate(Hook on, Thing other, Hook where) {
		this.location = new Location.Relative(this, other, on, where);		
	}

	@Override
	public boolean contains(float x, float y) {
		return true;
	}

	@Override
	public float outerx() {
		return x() + width();
	}

	@Override
	public float outery() {
		return y() + height();
	}

	@Override
	public void render(Window window, Graphics2D graphics, int width, int height, long delta) {
		
	}

	@Override
	public void update(Graphics2D graphics) {
		//Window Title etc
	}

	@Override
	public void tostate(int state) {
		
	}

	public boolean isResizing() {
		return resizing;
	}

	public void setResizing(boolean resizing) {
		this.resizing = resizing;
	}

	@Override
	public void bound(Bounds bound) {
		this.bounds = bound;
	}
	
	@Override
	public void locate(Location location) {
		this.location = location;
	}

	@Override
	public Bounds bounding() {
		return bounds;
	}

	@Override
	public void componentHidden(ComponentEvent e) {		
	}

	@Override
	public void componentMoved(ComponentEvent e) {		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if(!isUndecorated()) {
			size(getWidth(), getHeight());
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {		
	}

	@Override
	public int getMouseCursor(float x, float y) {
		return -1;
	}

	@Override
	public void minwidth(Value value) {
		context.set(MIN_WIDTH, value);
	}

	@Override
	public void maxwidth(Value value) {
		context.set(MAX_WIDTH, value);
	}

	@Override
	public void minheight(Value value) {
		context.set(MIN_HEIGHT, value);
	}

	@Override
	public void maxheight(Value value) {
		context.set(MAX_HEIGHT, value);
	}

	@Override
	public void minwidth(float width) {
		minwidth(new Value.StaticValue(width));
	}

	@Override
	public void maxwidth(float width) {
		maxwidth(new Value.StaticValue(width));
	}

	@Override
	public void minheight(float height) {
		minheight(new Value.StaticValue(height));
	}

	@Override
	public void maxheight(float height) {
		minheight(new Value.StaticValue(height));
	}

	public Region getToolbar() {
		return toolbar;
	}

	public void setToolbar(Region toolbar) {
		this.toolbar = toolbar;
	}

	@Override
	public void hover(float x, float y) {
		
	}

	
}
