package com.prism.model;

public class ReactiveText extends Text {

	private Object observed;

	public ReactiveText(Object observed) {
		this.observed = observed;
	}

	@Override
	public boolean isDirty() {
		String resolve = observed.toString();
		if(getTrueString() != resolve) {
			update(resolve);
			return true;
		}
		return false;
	}

}
