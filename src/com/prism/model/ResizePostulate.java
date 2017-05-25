package com.prism.model;

import java.awt.Cursor;

public class ResizePostulate {
	
	@Override
	public String toString() {
		return "ResizePostulate [cursor=" + cursor + ", dx=" + dx + ", dy="
				+ dy + ", dw=" + dw + ", dh=" + dh + "]";
	}
	public Cursor getCursor() {
		return cursor;
	}
	public float getDx() {
		return dx;
	}
	public float getDy() {
		return dy;
	}
	public float getDw() {
		return dw;
	}
	public float getDh() {
		return dh;
	}
	
	public ResizePostulate(int cursor, float dx, float dy, float dw, float dh) {
		this.cursor = Cursor.getPredefinedCursor(cursor);
		this.dx = dx;
		this.dy = dy;
		this.dw = dw;
		this.dh = dh;
	}
	
	private final Cursor cursor;
	private final float dx;
	private final float dy;
	private final float dw;
	private final float dh;
	

}
