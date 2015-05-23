package com.voodoodyne.rollbar;

import com.tapstream.rollbar.NotifyBuilder;
import org.json.JSONObject;
import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class MessageBuilder {

	public static final String NOTIFIER_VERSION = "1.0";

	private final NotifyBuilder notifyBuilder;

	@Inject
	public MessageBuilder(NotifyBuilder notifyBuilder) {
		this.notifyBuilder = notifyBuilder;
	}

	/**
	 * Build a JSON string that should be sent off to rollbar. We delegate to the wrapped notifierbuilder,
	 * then override some values.
	 */
	public String buildJson(String level, String message, Throwable throwable, Map<String, String> context) {
		final JSONObject json = notifyBuilder.build(level, message, throwable, context);

		json.put("uuid", UUID.randomUUID());

		JSONObject notifier = new JSONObject();
		notifier.put("name", "rollbar-appengine");
		notifier.put("version", NOTIFIER_VERSION);
		json.put("notifier", notifier);

		return json.toString();
	}
}
