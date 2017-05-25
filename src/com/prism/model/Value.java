package com.prism.model;

public interface Value {
	
	public static final Value MAX = new StaticValue(Float.MAX_VALUE);

	public float getValue();
	
	public static class StaticValue implements Value {
		
		public static final Value[] CONSTANTS;
		
		static {
			CONSTANTS = new Value[11];
			for(int f = 0; f <= 10; f++) {
				CONSTANTS[f] = new StaticValue(f);
			}
		}

		private float value;
		
		public StaticValue(float value) {
			this.value = value;
		}
		
		@Override
		public float getValue() {
			return value;
		}
		
		public void setValue(float value) {
			this.value = value;
		}
		
	}

}
