package org.shelly.session;

import javax.servlet.ServletContext;

import org.shelly.storage.Storage;

public class SessionConfig {

	private static final SessionConfig INSTANCE = new SessionConfig();

	private SessionConfig() {

	}

	public static final SessionConfig getInstance() {
		if (INSTANCE == null) {
			return new SessionConfig();
		}
		return INSTANCE;
	}

	public static final String DEFAULT_COOKIE_NAME = "SHELLY_SESSION_ID";
	public static final int DEFAULT_COOKIE_MAX_AGE = -1;
	public static final int DEFAULT_SESSION_TIMEOUT = 1800;

	private String cookieDomain;
	private String cookieName;
	private int cookieMaxAge;
	private String cookiePath;
	private int sessionTimeout;
	private Storage storage;
	private ServletContext context;

	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public int getCookieMaxAge() {
		return cookieMaxAge;
	}

	public void setCookieMaxAge(int cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public ServletContext getContext() {
		return context;
	}

	public void setContext(ServletContext context) {
		this.context = context;
	}

}