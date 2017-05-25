package com.prism.model;

import java.util.ArrayList;

public class Sequence {
	
	@Override
	public String toString() {
		return "Sequence [frames=" + frames + ", total=" + total + "]";
	}

	private ArrayList<Frame> frames;
	private final long total;
	
	public Sequence(Frame... frame) {
		frames = new ArrayList<Frame>();
		long counter = 0L;
		for(Frame f : frame) {
			frames.add(f);
			counter += f.lapse;
		}
		total = counter;
	}
	
	public Frame getFrame(int frame) {
		return frames.get(frame);
	}
	
	public int getFrames() {
		return frames.size();
	}
	
	public long getTotalTimeElapsed() {
		return total;
	}
	
}
