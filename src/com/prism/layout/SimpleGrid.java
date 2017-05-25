package com.prism.layout;

import com.prism.model.Bounds;
import com.prism.model.Location;
import com.prism.model.Thing;
import com.prism.model.Location.Hook;
import com.prism.model.ThingList;

public class SimpleGrid {

	private Thing parent;
	private float spacing;
	private int collumns;
	private int rows;
	
	public static final String GRID_PARENT  = "__GRID_PARENT";
	public static final String GRID_ROW     = "__GRID_ROW";
	public static final String GRID_COLLUMN = "__GRID_COLLUMNT";
	public static final String GRID_WIDTH   = "__GRID_WIDTH";
	public static final String GRID_HEIGHT  = "__GRID_HEIGHT";
	
	public SimpleGrid(Thing parent, int collumns, int rows) {
		this.parent = parent;
		this.spacing = 5F;
		this.collumns = collumns;
		this.rows = rows;
	}
	
	public SimpleGrid attach(Thing element) {
		return attach(element, 1, 1);
	}
	
	public SimpleGrid attach(Thing element, int w, int h) {
		boolean[][] taken = new boolean[rows][collumns];
		ThingList<Thing> components = parent.children().collect(GRID_PARENT, this);
		for(Thing thing : components) {
			int y = thing.getContext().$(GRID_ROW, Integer.class);
			int x = thing.getContext().$(GRID_COLLUMN, Integer.class);
			int height = thing.getContext().$(GRID_HEIGHT, Integer.class);
			int width  = thing.getContext().$(GRID_WIDTH, Integer.class);
			for(int oy = y; oy < (y+height); oy++) {
				for(int ox = x; ox < (x+width); ox++) {
					taken[oy][ox] = true;
				}
			}
		}
		for(int y = 0; y < taken.length; y++) {
			for(int x =0; x < taken[y].length; x++) {
				if(!taken[y][x]) {
					attach(element, y, x, w, h);
					return this;
				}
			}
		}
		return this;
	}
	
	public SimpleGrid attach(final Thing element, final int y, final int x, final int width, final int height) {
		Bounds bounds = new Bounds() {
			@Override
			public float getWidth() {
				return (getParent().width()/collumns*width) - spacing*2;
			}
			@Override
			public float getHeight() {
				return (getParent().height()/rows*height) - spacing*2;
			}
		};
		Location location = new Location() {
			@Override
			public float getX() {
				return getParent().x() + (x * (getParent().width()/collumns)) + spacing;
			}
			@Override
			public float getY() {
				return getParent().y() + (y * (getParent().height()/rows)) + spacing;
			}
		};
		element.getContext().set(GRID_PARENT, this);
		element.getContext().set(GRID_ROW, y);
		element.getContext().set(GRID_COLLUMN, x);
		element.getContext().set(GRID_WIDTH, width);
		element.getContext().set(GRID_HEIGHT, height);
		element.bound(bounds);
		element.locate(location);
		parent.attach(element);
		return this;
	}
	
	public void resize(int rows, int collumns) {
		this.rows = rows;
		this.collumns = collumns;
	}

	public float getSpacing() {
		return spacing;
	}

	public void setSpacing(float spacing) {
		this.spacing = spacing;
	}

	public void resize(Thing thing, int width, int height) {
		if(!thing.getContext().is(GRID_PARENT, this)) {
			return;
		}
		parent.detach(thing);
		attach(thing, thing.getContext().$(GRID_ROW, Integer.class),thing.getContext().$(GRID_COLLUMN, Integer.class), width, height);
	}
	
	public void move(Thing thing, int y, int x) {
		if(!thing.getContext().is(GRID_PARENT, this)) {
			return;
		}
		parent.detach(thing);
		attach(thing, y, x, thing.getContext().$(GRID_WIDTH, Integer.class),thing.getContext().$(GRID_HEIGHT, Integer.class));
	}
	
	public void alter(Thing thing, int y, int x, int width, int height) {
		if(!thing.getContext().is(GRID_PARENT, this)) {
			return;
		}
		parent.detach(thing);
		attach(thing, y, x, width, height);
	}
	
	public Thing getParent() {
		return parent;
	}

	public void setParent(Thing parent) {
		this.parent = parent;
	}
	
}
