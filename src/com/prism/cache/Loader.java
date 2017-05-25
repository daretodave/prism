package com.prism.cache;

import java.io.InputStream;

public interface Loader<E> {
	
	public E load(InputStream input, String title) throws Exception;
	
}