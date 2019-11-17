package org.shelly.session;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shelly.storage.Storage;

public class DistributedSessionFilter implements Filter {

	private ServletContext context;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.context = filterConfig.getServletContext();
		String configFile = filterConfig.getInitParameter("configFile");
		if (configFile == null || "".equals(configFile.trim())) {
			configFile = "config.properties";
		}
		initConfig(configFile);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		chain.doFilter(new DistributedSessionRequest((HttpServletRequest) request, (HttpServletResponse) response),
				response);
	}

	@Override
	public void destroy() {
		Storage storage = SessionConfig.getInstance().getStorage();
		if (storage != null) {
			storage.close();
		}
	}

	private void initConfig(String configFile) {
		InputStream in = getClass().getClassLoader().getResourceAsStream(configFile);
		Properties p = new Properties();
		try {
			p.load(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		String cookieDomain = p.getProperty("cookie.domain", "");
		String cookiePath = p.getProperty("cookie.path", "/");
		String cookieName = p.getProperty("cookie.name", SessionConfig.DEFAULT_COOKIE_NAME);
		String cookieMaxAgeStr = p.getProperty("cookie.maxAge");
		int cookieMaxAge = SessionConfig.DEFAULT_COOKIE_MAX_AGE;
		if (cookieMaxAgeStr != null && !"".equals(cookieMaxAgeStr.trim())) {
			try {
				cookieMaxAge = Integer.valueOf(cookieMaxAgeStr);
			} catch (Exception e) {
			}
		}
		String sessionTimeoutStr = p.getProperty("session.timeout");
		int sessionTimeout = SessionConfig.DEFAULT_SESSION_TIMEOUT;
		if (sessionTimeoutStr != null && !"".equals(sessionTimeoutStr.trim())) {
			try {
				sessionTimeout = Integer.valueOf(sessionTimeoutStr);
			} catch (Exception e) {
			}
		}
		Storage storage = null;
		String storageClass = p.getProperty("session.storage");
		if (storageClass == null || "".equals(storageClass.trim())) {
			throw new RuntimeException("Please configure session.storage for org.shelly.session");
		} else {
			try {
				storage = (Storage) Class.forName(storageClass).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		storage.start(p);
		SessionConfig config = SessionConfig.getInstance();
		config.setContext(context);
		config.setCookieDomain(cookieDomain);
		config.setCookieMaxAge(cookieMaxAge);
		config.setCookieName(cookieName);
		config.setCookiePath(cookiePath);
		config.setSessionTimeout(sessionTimeout);
		config.setStorage(storage);
	}

}