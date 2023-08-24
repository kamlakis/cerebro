package net.lakis.cerebro.web.sessions;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;

@Priority(1)
public class SessionsFilter implements ContainerResponseFilter, ContainerRequestFilter {
	public static final String SESSION_KEY = "asd123zz";

	@Context
	private ResourceInfo resourceInfo;

	private AbstractSessionProvider<?> sessionProvider;

	public SessionsFilter(AbstractSessionProvider<?> sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	// Request Filter
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		if (resourceInfo.getResourceClass().getAnnotation(Stateful.class) == null
				&& resourceInfo.getResourceMethod().getAnnotation(Stateful.class) == null)
			return;

		ISession session = null;
		Cookie cookie = requestContext.getCookies().get(SESSION_KEY);
		if (cookie != null)
			session = sessionProvider.getSession(cookie.getValue());

		if (session == null)
			session = sessionProvider.createSession();

		requestContext.setProperty(SESSION_KEY, session);

	}

	// Response Filter
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		ISession session = sessionProvider.getSession(requestContext);
		if (session == null)
			return;

		sessionProvider.putSessionObject(session);

		Cookie cookie = requestContext.getCookies().get(SESSION_KEY);
		if (cookie != null && session.getId().equals(cookie.getValue()))
			return;

		responseContext.getHeaders().add(HttpHeaders.SET_COOKIE, //
				new NewCookie(SESSION_KEY, session.getId()));
	}

}
