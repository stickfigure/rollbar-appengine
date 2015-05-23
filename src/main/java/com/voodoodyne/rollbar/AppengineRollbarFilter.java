package com.voodoodyne.rollbar;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.tapstream.rollbar.RollbarFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Modifies behavior of RollbarFilter to send all exceptions that bubble through this filter off to Rollbar.
 * Does not use logback in any way and does not catch log messages.
 */
@Singleton
public class AppengineRollbarFilter extends RollbarFilter {
	private final MessageBuilder messageBuilder;

	@Inject
	public AppengineRollbarFilter(MessageBuilder messageBuilder) {
		this.messageBuilder = messageBuilder;
	}

	/**
	 * This is run after the RollbarFilter and so the MDC has been set up.
	 */
	@RequiredArgsConstructor
	private class TrappingFilterChain implements FilterChain {
		private final FilterChain base;

		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			try {
				base.doFilter(request, response);
			} catch (IOException | ServletException | RuntimeException e) {
				QueueFactory.getDefaultQueue().add(TaskOptions.Builder.withPayload(new RollbarTask(messageBuilder.buildJson("ERROR", e.toString(), e, MDC.getCopyOfContextMap()))));
				throw e;
			}
		}
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		super.doFilter(servletRequest, servletResponse, new TrappingFilterChain(filterChain));
	}
}
