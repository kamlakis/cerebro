package net.lakis.cerebro.web.sessions;

import javax.ws.rs.container.ContainerRequestContext;
import static net.lakis.cerebro.web.sessions.SessionsFilter.SESSION_KEY;

public abstract class AbstractSessionProvider<T extends ISession> {
	public abstract T getSession(String id);

	public abstract void putSession(T session);

	public abstract T createSession();

	@SuppressWarnings("unchecked")
	public T getSession(ContainerRequestContext request) {
		return (T) request.getProperty(SESSION_KEY);
	}
	
	 @SuppressWarnings("unchecked")
	void putSessionObject(Object session) {
		 this.putSession((T) session);
	 }
 
}
