package org.shelly.storage.memcached;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.spy.memcached.MemcachedClient;

public class Memcached {

	private MemcachedClient memcached = null;

	private static class Inner {
		private static final Memcached INSTANCE = new Memcached();
	}

	private Memcached() {
		
	}

	public static Memcached getInstance() {
		return Inner.INSTANCE;
	}

	protected void init(MemcachedClient memcached) {
		this.memcached = memcached;
	}

	public String set(String key, Object value) {
		if (value == null) {
			memcached.delete(key);
			return null;
		} else {
			try {
				Future<Boolean> fo = memcached.set(key, -1, value);
				return fo.get().toString();
			} catch (InterruptedException | ExecutionException e) {
				return null;
			}
		}
	}

	public String setex(String key, int seconds, Object value) {
		if (value == null) {
			memcached.delete(key);
			return null;
		} else {
			try {
				Future<Boolean> fo = memcached.set(key, seconds, value);
				return fo.get().toString();
			} catch (InterruptedException | ExecutionException e) {
				return null;
			}
		}
	}

	public <T> T get(Class<T> clazz, String key) {
		Object obj = memcached.get(key);
		if (obj == null) {
			return null;
		}
		return clazz.cast(obj);
	}

	public void remove(String key) {
		memcached.delete(key);
	}

	public void expire(String key, int expiredTime) {
		memcached.touch(key, expiredTime);
	}

	public void close() {
		memcached.shutdown();
	}

}