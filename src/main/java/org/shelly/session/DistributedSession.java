package org.shelly.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.shelly.storage.Storage;

@SuppressWarnings("deprecation")
public class DistributedSession implements HttpSession {

	private final SessionConfig config = SessionConfig.getInstance();
	private final Storage storage = config.getStorage();
	private final String cookieName = config.getCookieName();
	private final int maxAlive = config.getSessionTimeout();

	private final String id;
	private final String sessionKey;
	private Session session;
	private boolean invalid = false;

	public DistributedSession(String id) {
		this.id = id;
		this.sessionKey = cookieName + ":" + id;
		session = storage.get(Session.class, sessionKey);
		if (session == null) {
			initSession();
			session.setLastAccessTime(System.currentTimeMillis());
			storage.put(sessionKey, session, maxAlive);
		} else {
			if (session.isNew()) {
				session.setNew(false);
			}
			storage.expire(sessionKey, maxAlive);
		}
	}

	private void initSession() {
		session = new Session();
		session.setNew(true);
	}

	private void checkSessionInvalid() {
		if (invalid) {
			throw new IllegalStateException("Session is invalid.");
		}
	}

	private void checkSerializable(Object value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (!Serializable.class.isInstance(value)) {
			throw new RuntimeException(value.getClass().getName() + " NotSerializable  ");
		}
	}

	@Override
	public long getCreationTime() {
		return session.getCreateTime();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getLastAccessedTime() {
		return session.getLastAccessTime();
	}

	@Override
	public ServletContext getServletContext() {
		return config.getContext();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		// ignore
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxAlive;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		throw new UnsupportedOperationException("Not supported .");
	}

	@Override
	public Object getAttribute(String attributeName) {
		checkSessionInvalid();
		Serializable attribute = session.getAttribute(attributeName);
		return attribute;
	}

	@Override
	public Object getValue(String name) {
		return getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		checkSessionInvalid();
		Set<String> attributeNameSet = session.getAttributeNames();
		return new Enumerator(attributeNameSet);
	}

	@Override
	public String[] getValueNames() {
		Enumeration<String> attributeNames = getAttributeNames();
		List<String> attributeNameList = new ArrayList<String>();
		while (attributeNames.hasMoreElements()) {
			attributeNameList.add((String) attributeNames.nextElement());
		}
		return attributeNameList.toArray(new String[0]);
	}

	@Override
	public void setAttribute(String attributeName, Object attributeValue) {
		checkSessionInvalid();
		checkSerializable(attributeValue);
		session.putAttribute(attributeName, (Serializable) attributeValue);
		storage.put(sessionKey, session, maxAlive);
	}

	@Override
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String attributeName) {
		checkSessionInvalid();
		session.removeAttribute(attributeName);
		if (session.isEmpty()) {
			storage.remove(sessionKey);
		} else {
			storage.put(sessionKey, session, maxAlive);
		}
	}

	@Override
	public void removeValue(String name) {
		removeAttribute(name);
	}

	@Override
	public void invalidate() {
		invalid = true;
		storage.remove(sessionKey);
	}

	@Override
	public boolean isNew() {
		checkSessionInvalid();
		return session.isNew();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DistributedSession other = (DistributedSession) obj;
		if ((id == null) ? (other.getId() != null) : !id.equals(other.getId())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Session id is ");
		sb.append(this.id);
		sb.append(" ,detail is ");
		sb.append(session);
		return sb.toString();
	}

	private static class Enumerator implements Enumeration<String> {

		private Iterator<String> iterator;

		public Enumerator(Set<String> attributeNames) {
			this.iterator = attributeNames.iterator();
		}

		@Override
		public boolean hasMoreElements() {
			return iterator.hasNext();
		}

		@Override
		public String nextElement() {
			return iterator.next();
		}

	}

}