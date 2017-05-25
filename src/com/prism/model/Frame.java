package com.prism.model;

import java.util.Arrays;

public class Frame {
	
	@Override
	public String toString() {
		return "Frame [arotation=" + Arrays.toString(arotation) + ", ascalex="
				+ Arrays.toString(ascalex) + ", ascaley="
				+ Arrays.toString(ascaley) + ", atranslation="
				+ Arrays.toString(atranslation) + ", aopacity=" + aopacity
				+ ", brotation=" + Arrays.toString(brotation) + ", bscalex="
				+ Arrays.toString(bscalex) + ", bscaley="
				+ Arrays.toString(bscaley) + ", btranslation="
				+ Arrays.toString(btranslation) + ", bopacity=" + bopacity
				+ ", lapse=" + lapse + "]\n";
	}

	public Frame(Float[] arotation, Float[] ascalex, Float[] ascaley,
			Float[] atranslation, Float aopacity, Float[] brotation,
			Float[] bscalex, Float[] bscaley, Float[] btranslation,
			Float bopacity, Float lapse) {
		this.arotation = arotation;
		this.ascalex = ascalex;
		this.ascaley = ascaley;
		this.atranslation = atranslation;
		this.aopacity = aopacity;
		this.brotation = brotation;
		this.bscalex = bscalex;
		this.bscaley = bscaley;
		this.btranslation = btranslation;
		this.bopacity = bopacity;
		this.lapse = lapse;
	}

	public Float[] arotation; //x ratio, y ratio, rotation
	public Float[] ascalex;   //x ratio, y ratio, scalex
	public Float[] ascaley;   //x ratio, y ratio, scaley
	public Float[] atranslation; //x, y
	public Float   aopacity;
	
	public Float[] brotation; //x ratio, y ratio, rotation
	public Float[] bscalex;   //x ratio, y ratio, scalex
	public Float[] bscaley;   //x ratio, y ratio, scaley
	public Float[] btranslation; //x, y
	public Float   bopacity;
	
	public Float lapse;

	public void adhere(State state, boolean start) {
		if(start) {
			if(aopacity != null) 
				state.setOpacity(aopacity);
			if(atranslation != null) {
				state.setTx(atranslation[0]);
				state.setTy(atranslation[1]);
			}
			if(ascalex != null) {
				state.setScalexx(ascalex[0]);
				state.setScalexy(ascalex[1]);
				state.setScalex (ascalex[2]);
			}
			if(ascaley != null) {
				state.setScaleyx(ascaley[0]);
				state.setScaleyy(ascaley[1]);
				state.setScaley (ascaley[2]);
			}
			if(arotation != null) {
				state.setRotationx(arotation[0]);
				state.setRotationy(arotation[1]);
				state.setRotation (arotation[2]);
			}
		} else {
			if(bopacity != null) 
				state.setOpacity(bopacity);
			if(btranslation != null) {
				state.setTx(btranslation[0]);
				state.setTy(btranslation[1]);
			}
			if(bscalex != null) {
				state.setScalexx(bscalex[0]);
				state.setScalexy(bscalex[1]);
				state.setScalex (bscalex[2]);
			}
			if(bscaley != null) {
				state.setScaleyx(bscaley[0]);
				state.setScaleyy(bscaley[1]);
				state.setScaley (bscaley[2]);
			}
			if(brotation != null) {
				state.setRotationx(brotation[0]);
				state.setRotationy(brotation[1]);
				state.setRotation (brotation[2]);
			}
		}
	}

	public void adhere(State state, float ratio) {
		if(aopacity != null && bopacity != null) {
			state.setOpacity(calculate(aopacity, bopacity, ratio));
		}
		if(arotation != null && brotation != null) {
			state.setRotationx(calculate(arotation[0], brotation[0], ratio));
			state.setRotationy(calculate(arotation[1], brotation[1], ratio));
			state.setRotation (calculate(arotation[2], brotation[2], ratio));
		}
		if(ascalex != null && bscalex != null) {
			state.setScalexx(calculate(ascalex[0], bscalex[0], ratio));
			state.setScalexy(calculate(ascalex[1], bscalex[1], ratio));
			state.setScalex (calculate(ascalex[2], bscalex[2], ratio));
		}
		if(ascaley != null && bscaley != null) {
			state.setScaleyx(calculate(ascaley[0], bscaley[0], ratio));
			state.setScaleyy(calculate(ascaley[1], bscaley[1], ratio));
			state.setScaley (calculate(ascaley[2], bscaley[2], ratio));
		}
		if(arotation != null && brotation != null) {
			state.setRotationx(calculate(arotation[0], brotation[0], ratio));
			state.setRotationy(calculate(arotation[1], brotation[1], ratio));
			state.setRotation (calculate(arotation[2], brotation[2], ratio));
		}
		if(atranslation != null && btranslation  != null) {
			state.setTx(calculate(atranslation[0], btranslation[0], ratio));
			state.setTy(calculate(atranslation[1], btranslation[1], ratio));
		}
	}

	private static float calculate(Float a, Float b, float ratio) {
		return a + ((b-a) * ratio);
	}
}
