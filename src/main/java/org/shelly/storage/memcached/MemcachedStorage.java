package org.shelly.storage.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import org.shelly.storage.Storage;

import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

public class MemcachedStorage implements Storage {

	private final Memcached memcached = Memcached.getInstance();

	@Override
	public void start(Properties props) {
		props = getProviderProperties(props);
		String hostname = getProperty(props, "hostname", "127.0.0.1");
		Integer port = getProperty(props, "port", 11211);
		MemcachedClient mcc = null;
		try {
			mcc = new MemcachedClient(new BinaryConnectionFactory(),
					Arrays.asList(new InetSocketAddress(hostname, port)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		memcached.init(mcc);
	}

	@Override
	public void close() {
		memcached.close();
	}

	@Override
	public void put(String key, Object value, int expiredTime) {
		if (expiredTime <= 0) {
			memcached.set(key, value);
		} else {
			memcached.setex(key, expiredTime, value);
		}
	}

	@Override
	public <T> T get(Class<T> clazz, String key) {
		return memcached.get(clazz, key);
	}

	@Override
	public void remove(String key) {
		memcached.remove(key);
	}

	@Override
	public void expire(String sessionKey, int expiredTime) {
		memcached.expire(sessionKey, expiredTime);
	}

	private final static Properties getProviderProperties(Properties props) {
		Properties new_props = new Properties();
		Enumeration<Object> keys = props.keys();
		String prefix = "memcached.";
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(prefix)) {
				new_props.setProperty(key.substring(prefix.length()), props.getProperty(key));
			}
		}
		return new_props;
	}

	private static String getProperty(Properties props, String key, String defaultValue) {
		String value = props.getProperty(key, defaultValue);
		return value == null ? value : value.trim();
	}

	private static int getProperty(Properties props, String key, int defaultValue) {
		try {
			String value = props.getProperty(key);
			return value == null ? defaultValue : Integer.parseInt(value.trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

}