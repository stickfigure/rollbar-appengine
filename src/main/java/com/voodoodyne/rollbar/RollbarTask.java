package com.voodoodyne.rollbar;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.tapstream.rollbar.HttpRequest;
import com.tapstream.rollbar.HttpRequester;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.net.URL;

/**
 * Send the report off to rollbar. Any errors are sent upstream as RollbarException; otherwise we might
 * trigger a cascading avalanche of new errors...
 */
@Slf4j
@RequiredArgsConstructor
public class RollbarTask implements DeferredTask {

	private final String json;

	@Override
	public void run() {
		log.debug("Uploading to Rollbar: {}", json);

		try {
			final URL url = new URL("https://api.rollbar.com/api/1/item/");

			final HttpRequest request = new HttpRequest(url, "POST");
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Accept", "application/json");
			request.setBody(json);

			final int status = new HttpRequester().send(request);
			if (status < 200 || status >= 300)
				throw new RollbarException("Http request produced bad status code " + status);
		} catch (RollbarException e) {
			throw e;
		} catch (Throwable e) {
			throw new RollbarException(e);
		}
	}
}
