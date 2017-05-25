package com.prism.model;

public class State {
	
	public float getScalex() {
		return scalex;
	}

	public void setScalex(float scalex) {
		this.scalex = scalex;
	}

	public float getScalexx() {
		return scalexx;
	}

	public void setScalexx(float scalexx) {
		this.scalexx = scalexx;
	}

	public float getScalexy() {
		return scalexy;
	}

	public void setScalexy(float scalexy) {
		this.scalexy = scalexy;
	}

	public float getScaley() {
		return scaley;
	}

	public void setScaley(float scaley) {
		this.scaley = scaley;
	}

	public float getScaleyx() {
		return scaleyx;
	}

	public void setScaleyx(float scaleyx) {
		this.scaleyx = scaleyx;
	}

	public float getScaleyy() {
		return scaleyy;
	}

	public void setScaleyy(float scaleyy) {
		this.scaleyy = scaleyy;
	}

	public float getTx() {
		return tx;
	}

	public void setTx(float tx) {
		this.tx = tx;
	}

	public float getTy() {
		return ty;
	}

	public void setTy(float ty) {
		this.ty = ty;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	private float tx;
	private float ty;
	private float opacity;
	
	private float rotation;
	private float rotationx;
	private float rotationy;
	
	private float scalex;
	private float scalexx;
	private float scalexy;
	
	private float scaley;
	private float scaleyx;
	private float scaleyy;
	
	public State() {
		opacity = 1L;
		scalex  = 1F;
		scaley  = 1F;
		rotationx = .5F;
		rotationy = .5F;
		scalexx   = .5F;
		scalexy   = .5F;
		scaleyx   = .5F;
		scaleyy   = .5F;
	}

	public float getRotationx() {
		return rotationx;
	}

	public void setRotationx(float rotationx) {
		this.rotationx = rotationx;
	}

	public float getRotationy() {
		return rotationy;
	}

	public void setRotationy(float rotationy) {
		this.rotationy = rotationy;
	}

}
