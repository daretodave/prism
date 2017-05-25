package com.prism.model;


public abstract class Location {

	private float alpha;
	private float beta;

	public void translate(float x, float y) {
		this.alpha += x;
		this.beta  += y;
	}

	public float getAlpha() {
		return alpha;
	}

	public float getBeta() {
		return beta;
	}

	public enum Hook {

		NW(0F, 0F),
		CW (0F, 0.5F),
		SW(0F, 1.0F),

		NC(.5F, 0F),
		 C(.5F, .5F),
		SC(.5F, 1.0F),

		NE(1.0F, 0F),
		CE (1.0F, 0.5F),
		SE(1.0F, 1.0F);

		private float xRatio;
		private float yRatio;

		Hook(float xRatio, float yRatio) {
			this.xRatio = xRatio;
			this.yRatio = yRatio;
		}

		public float x(float width) {
			return xRatio * width;
		}

		public float y(float height) {
			return yRatio * height;
		}

	}

	public static class Mold extends Location {

		private float alpha;
		private float beta;

		public Mold(float alpha, float beta) {
			this.alpha = alpha;
			this.beta = beta;
		}

		@Override
		public float getX() {
			return alpha;
		}

		@Override
		public float getY() {
			return beta;
		}

		public void setX(float x) {
			alpha = x;
		}

		public void setY(float y) {
			beta = y;
		}

		@Override
		public void translate(float x, float y) {
			this.alpha += x;
			this.beta += y;
		}
	}

	public static class Relative extends Location {

		private Thing[] structures;
		private Hook[] hooks;

		public Relative(Thing a, Thing b, Hook aHook, Hook bHook) {
			structures = new Thing[2];
			hooks      = new Hook[2];
			if(a == b)
				throw new RuntimeException("Equal relativity");
			structures[0] = a;
			structures[1] = b;
			hooks[0]      = aHook;
			hooks[1]	  = bHook;
		}

		@Override
		public float getX() {
			return structures[1].x()
					+ hooks[1].x((structures[1].width()/**structures[1].getScaleX()*/))
					- hooks[0].x((structures[0].width()/**structures[0].getScaleX()*/));
		}

		@Override
		public float getY() {
			return structures[1].y()
					+ hooks[1].y((structures[1].height()/**structures[1].getScaleY()*/))
					- hooks[0].y((structures[0].height()/**structures[1].getScaleY()*/));
		}

	}

	public float x() {
		return getX() + alpha;
	}

	public float y() {
		return getY() + beta;
	}

	public abstract float getX();

	public abstract float getY();

	public void setTranslate(float sx, float sy) {
		alpha = sx;
		beta  = sy;
	}

}
