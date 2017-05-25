package com.prism.cache;

public class Asset<T> {
	
	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	private String folder;
	private String asset;
	
	public Asset(String folder, String asset) {
		this.folder = folder;
		this.asset  = asset;
	}
	
	@SuppressWarnings("unchecked")
	public T getAsset() {
		return (T) Cache.asset(asset, folder);
	}
	
}
