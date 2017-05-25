package com.prism;

import com.prism.model.Component;

public abstract class WindowDefinition {
	
	public Window window;

	public abstract void assets();
	
	public abstract void build(Context context);	
	
	public abstract void construct(Window window);
	
	public void open() {
		if(window == null) {
			window = Prism.build(this);
		}
		window.open();
	}
	
	public void sequence(Component...elements) {
		if(elements.length < 2) {
			throw new RuntimeException("Can create a tab sequence with fewer then 2 elements");
		}
		for(int i = 0; i < elements.length-1; i++) {
			elements[i].tabto(elements[i+1]);
		}
		elements[elements.length-1].tabto(elements[0]);		
	}
	
}
