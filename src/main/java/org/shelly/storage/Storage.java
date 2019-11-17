package org.shelly.storage;

import java.util.Properties;

/**
 * @FileName: Storage.java
 * @CreateTime: 2019年10月10日上午10:22:04
 * @Description: 标准存储接口
 * @Author: Shelly
 */
public interface Storage {

	void start(Properties properties);

	void close();

	void put(String key, Object value, int expiredTime);

	<T> T get(Class<T> clazz, String key);

	void remove(String key);

	void expire(String sessionKey, int expiredTime);

}