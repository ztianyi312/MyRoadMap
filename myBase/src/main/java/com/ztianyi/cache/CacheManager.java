package com.ztianyi.cache;

public interface CacheManager {

	public abstract Object get(String s) throws Exception ;
	
	public abstract Object load(String s) throws Exception;
	
	public abstract void update(String s);
}
