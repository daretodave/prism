package com.prism;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.prism.Window.Decoration;
import com.prism.cache.Cache;
import com.prism.logic.Input;
import com.prism.logic.Renderer;
import com.prism.model.Bounds;
import com.prism.model.Drawable;
import com.prism.model.Location.Hook;
import com.prism.model.Region;
import com.prism.model.ResizePostulate;
import com.prism.model.Thing;

public class Prism implements Renderer, Input {

	private static final int DEFAULT_POOL_COUNT = 4;
	private static List<Window> windows = new ArrayList<Window>();
	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(DEFAULT_POOL_COUNT);
	private static Prism INSTANCE = new Prism();


	public static final String PRESS_LOCATION_X = "__PRESS_LOCATION_X";
	public static final String PRESS_LOCATION_Y = "__PRESS_LOCATION_Y";
	public static final String RESIZE_POSULTATE = "__RESIZE_POSTULATE";
	public static final String LOCKED           = "__LOCKED";

	public static final float RESIZE_OFFSET = 5F;
	private static boolean aliveWhenNoWindowsAreAlive;

	static {
		Cache.load("default_styles");
		Cache.load("default_fonts");
		Cache.load("default_anims");
	}

	public static Window build(WindowDefinition defined) {
		Context context = new Context();
		defined.assets();
		defined.build(context);
		defined.window = build(context);
		defined.construct(defined.window);
		return defined.window;
	}

	public static Window build(Context context) {
		Window window = new Window(
				context.$(Window.TITLE, String.class,   new String()),
				context.$(Window.WIDTH, Integer.class,  new Integer(800)),
				context.$(Window.HEIGHT, Integer.class, new Integer(500)),
				INSTANCE, INSTANCE
		);
		window.setContext(context);
		Window.Decoration decoration = context.$(Window.DECORATION, Decoration.class, Decoration.DECORATED);
		switch(decoration) {
		case PRISM:
			window.setToolbar(new Region(new Bounds.Bounded(window, Bounds.Scalar.WIDTH, 0F, 20F)));
		case UNDECORATED:
			window.setUndecorated(true);
			break;
		default:
			break;
		}
		if(context.is(Window.TOP_ALWAYS)) {
			window.setAlwaysOnTop(true);
		}
		if(context.exist(Window.ICON)) {
			window.setIconImage(context.$(Window.ICON, BufferedImage.class));
		}
		window.setIgnoreRepaint(true);
		if(!decoration.equals(Decoration.DECORATED) && context.is(Window.RESIZABLE, true)) {
			Region[] regions = new Region[8];

			Region north = new Region(new Bounds.Bounded(window, Bounds.Scalar.WIDTH, RESIZE_OFFSET*2, RESIZE_OFFSET));
			north.locate(window, Hook.NC);
			north.getContext().set(RESIZE_POSULTATE, new ResizePostulate(Cursor.N_RESIZE_CURSOR, 0, 1F, 0, -1));
			regions[0] = north;

			Region south = new Region(new Bounds.Bounded(window, Bounds.Scalar.WIDTH, RESIZE_OFFSET*2, RESIZE_OFFSET));
			south.locate(window, Hook.SC);
			south.getContext().set(RESIZE_POSULTATE, new ResizePostulate(Cursor.S_RESIZE_CURSOR, 0, 0, 0, 1F));
			regions[1] = south;

			Region west = new Region(new Bounds.Bounded(window, Bounds.Scalar.HEIGHT, RESIZE_OFFSET*2, RESIZE_OFFSET));
			west.locate(window, Hook.CW);
			west.getContext().set(RESIZE_POSULTATE, new ResizePostulate(Cursor.W_RESIZE_CURSOR, 1, 0, -1, 0));
			regions[2] = west;

			Region east = new Region(new Bounds.Bounded(window, Bounds.Scalar.HEIGHT, RESIZE_OFFSET*2, RESIZE_OFFSET));
			east.locate(window, Hook.CE);
			east.getContext().set(RESIZE_POSULTATE, new ResizePostulate(Cursor.E_RESIZE_CURSOR, 0, 0, 1, 0));
			regions[3] = east;

			Region nw = new Region(new Bounds.Mold(RESIZE_OFFSET, RESIZE_OFFSET));
			nw.locate(window, Hook.NW);
			nw.getContext().set(RESIZE_POSULTATE, new ResizePostulate(Cursor.NW_RESIZE_CURSOR, 1, 1F, -1F, -1));
			regions[4] = nw;

			Region sw = new Region(new Bounds.Mold(RESIZE_OFFSET, RESIZE_OFFSET));
			sw.locate(window, Hook.SW);
			sw.getContext().set(RESIZE_POSULTATE, new ResizePostulate(Cursor.SW_RESIZE_CURSOR, 1, 0, -1, 1));
			regions[5] = sw;

			Region ne = new Region(new Bounds.Mold(RESIZE_OFFSET, RESIZE_OFFSET));
			ne.locate(window, Hook.NE);
			ne.getContext().set(RESIZE_POSULTATE, new ResizePostulate(Cursor.NE_RESIZE_CURSOR, 0, 1, 1, -1));
			regions[6] = ne;

			Region se = new Region(new Bounds.Mold(RESIZE_OFFSET, RESIZE_OFFSET));
			se.locate(window, Hook.SE);
			se.getContext().set(RESIZE_POSULTATE, new ResizePostulate(Cursor.SE_RESIZE_CURSOR, 0, 0, 1, 1));
			regions[7] = se;

			window.attach(north);
			window.attach(south);
			window.attach(west);
			window.attach(east);
			window.attach(nw);
			window.attach(ne);
			window.attach(sw);
			window.attach(se);

			window.setResize(regions);
		}
		windows.add(window);
		return window;
	}

	public static Future<?> submit(Runnable runnable) {
		return service.submit(runnable);
	}

	public static ScheduledFuture<?> schedule(Runnable runnable, long when) {
		return service.schedule(runnable, when, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> repeat(Runnable runnable, long interval, boolean delay) {
		return service.scheduleAtFixedRate(runnable, delay ? interval : 0, interval, TimeUnit.MILLISECONDS);
	}

	public static void setPoolSize(int size) {
		service = Executors.newScheduledThreadPool(size);
	}

	public static boolean destroy(Window window) {
		boolean b = windows.remove(window);
		if(!b) {
			return b;
		}
		if(windows.isEmpty() && !Prism.isAliveWhenNoWindowsAreAlive()) {
			System.exit(-1);
		}
		return true;
	}

	@Override
	public void mouse(Window window, boolean click, int x, int y,
			boolean control, boolean shift, boolean hover) {
		if(!click && !hover) {
			window.getContext().set(PRESS_LOCATION_X, x);
			window.getContext().set(PRESS_LOCATION_Y, y);
			if(!window.mouse(x, y, Thing.PRESS, control, shift, false)) {
				window.getContext().set(LOCKED, true);
			}
		} else if(!hover) {
			window.getContext().set(LOCKED, false);
			window.mouse(x, y, Thing.RELEASE, control, shift, false);
		} else if(hover) {
			window.mouse(x, y, Thing.HOVER, control, shift, true);
		}

	}

	@Override
	public void key(Window window, boolean press, int key, String text,
			boolean control, boolean shift) {
		window.key(key, !press, text, control, shift);
	}

	@Override
	public void render(Window window, Graphics2D graphic, int width, int height) {
		graphic.setRenderingHint(
		        RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		graphic.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphic.setRenderingHint(
		        RenderingHints.KEY_FRACTIONALMETRICS,
		        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		Drawable background = window.getContext().$(Window.BACKGROUND, Drawable.class);
		if(background != null) {
			background.render(graphic, width, height);
		}
		long delta = System.currentTimeMillis();
		window.children().render(window, graphic, width, height, delta);
	}

	@Override
	public void drag(Window window, int x, int y, boolean control, boolean shift) {
		if(window.drag(x, y)) {
			if(!window.getContext().$(Window.DECORATION, Window.Decoration.class, Window.Decoration.DECORATED).equals(Window.Decoration.DECORATED) && window.getContext().is(Window.DRAGABLE, true) && window.getContext().is(LOCKED)) {
				int dx = x - window.getContext().$(PRESS_LOCATION_X, Integer.class);
				int dy = y - window.getContext().$(PRESS_LOCATION_Y, Integer.class);
				window.translate(dx, dy);
			}
		}
	}

	public static boolean isAliveWhenNoWindowsAreAlive() {
		return aliveWhenNoWindowsAreAlive;
	}

	public static void setAliveWhenNoWindowsAreAlive(
			boolean aliveWhenNoWindowsAreAlive) {
		Prism.aliveWhenNoWindowsAreAlive = aliveWhenNoWindowsAreAlive;
	}

	public static String getClipboardContents() {
	    String result = "";
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    Transferable contents = clipboard.getContents(null);
	    if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
	      try {
	        result = (String)contents.getTransferData(DataFlavor.stringFlavor);
	      } catch (Exception ex){
	        ex.printStackTrace();
	      }
	    }
	    return result;
	  }

}
