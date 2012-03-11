package com.ztianyi.cache;


import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.rubyeye.xmemcached.MemcachedClient;

public abstract class XMemcachedCacheManager implements CacheManager {
	
	private ConcurrentMap<String,String> keyMap = new ConcurrentHashMap<String,String>();

	public static final String MUTEXKEY = "_MUTEX_";
	protected abstract  Object loadData(String key);
	protected abstract String getNamespace() ;

	protected abstract int getLogicExpire();
	
	protected int getExpireSpan() {
		return 30;
	}
	
	private int getPhysicalExpire() {
		return this.getLogicExpire()+this.getExpireSpan();
	}
	
	protected abstract MemcachedClient getClient();
	
	
	protected String getWholeKey(String key) {
		if(this.getNamespace() == null) {
			return key;
		}
		
		return this.getNamespace()+key;
	}
	
	@Override
	public Object get(String key) throws Exception {
		String wholeKey = this.getWholeKey(key);
		
		Holder holder = this.getClient().get(wholeKey);
		
		if(holder == null) {
			if(!this.getClient().add(wholeKey+MUTEXKEY, 60, 1)) {
				String lock = null;
				
				if((lock = keyMap.putIfAbsent(key, key)) == null)
				{
					lock = key;
				}
				synchronized(lock) {
					while((holder = this.getClient().get(wholeKey)) == null) {
						Thread.sleep(50);
						
						if(this.getClient().add(wholeKey+MUTEXKEY, 60, 1)) {
							Object o = null;
							try{
								o = this.load(wholeKey);
							} finally {
								this.getClient().deleteWithNoReply(wholeKey+MUTEXKEY);
							}
							return o;
						}
					}
				}
				
				
			} else {
				Object o = null;
				try{
					o = this.load(key);
				} finally {
					this.getClient().deleteWithNoReply(wholeKey+MUTEXKEY);
				}
				return o;
			}
		} else {
			if ((holder.getTime()) <= System.currentTimeMillis() &&
					this.getClient().add(wholeKey+MUTEXKEY, 60, 1)) {
				
				this.update(key);
			}
		}
		
		return holder.getData();
	}

	@Override
	public Object load(String key) throws Exception {
		
		Object o = this.loadData(key);
		
		String wholeKey = this.getWholeKey(key);
		this.getClient().set(wholeKey, this.getPhysicalExpire(), new Holder(o));
		return o;
	}

	@Override
	public void update(String key) {
		Thread t = new Thread(new Updater(key));
		t.start();
	}

	class Holder implements Serializable{
		
		private static final long serialVersionUID = 8564747759795922007L;
		private long time;
		private Object data;
		
		Holder(Object o) {
			this.data = o;
			this.time = System.currentTimeMillis()+XMemcachedCacheManager.this.getLogicExpire()*1000l;
		}

		public Object getData() {
			return data;
		}

		public long getTime() {
			return time;
		}

		
	}
	
	class Updater implements Runnable {

		private String key;
		
		Updater(String key) {
			this.key =key;
		}
		@Override
		public void run() {
			try {
				try {
					XMemcachedCacheManager.this.load(key);
				} finally {
					String wholeKey = XMemcachedCacheManager.this.getWholeKey(key);
					XMemcachedCacheManager.this.getClient().deleteWithNoReply(wholeKey+MUTEXKEY);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
		}
		
	}
}
