package com.prism.model;

public class Animation {

	private Sequence sequence;
	private AnimationListener listener;
	private long start;
	private int frame;
	private boolean finished;
	private float modifier;

	public Animation(Sequence sequence, float modifier) {
		this.sequence = sequence;
		this.start    = -1;
		this.frame    = -1;
		this.modifier = modifier;
	}

	public interface AnimationListener {
		public void onEnd(boolean canceled);
	}

	public void setAnimationListener(AnimationListener listener) {
		this.listener = listener;
	}

	public void animate(Component component, long delta) {
		if(finished) {
			if(listener != null) {
				listener.onEnd(true);
			}
			return;
		}
		if(frame == -1) {
			frame = 0;
			start = System.currentTimeMillis();
			sequence.getFrame(0).adhere(component.state, true);
		}
		Frame current = sequence.getFrame(frame);
		float lapse = delta-start;
		float ratio = lapse/(current.lapse*modifier);
		if(lapse >= (current.lapse*modifier)) {
			current.adhere(component.state, false);
			if(frame+1 >= sequence.getFrames()) {
				component.anim(null);
				finished = true;
				if(listener != null) {
					listener.onEnd(false);
				}
				return;
			}
			start = System.currentTimeMillis();
			frame++;
			return;
		}
		current.adhere(component.state, ratio);
	}

	public AnimationListener getListener() {
		return listener;
	}

	public void setListener(AnimationListener listener) {
		this.listener = listener;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public float getModifier() {
		return modifier;
	}

	public void setModifier(float modifier) {
		this.modifier = modifier;
	}


}
