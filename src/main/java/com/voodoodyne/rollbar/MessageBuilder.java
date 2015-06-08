package com.voodoodyne.rollbar;

import com.tapstream.rollbar.NotifyBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
public class MessageBuilder {
	/** Arbitrarily decide that 500 stack frames is the limit */
	private static final int MAX_STACK_FRAMES = 500;

	public static final String NOTIFIER_VERSION = "1.0";

	private final NotifyBuilder notifyBuilder;

	@Inject
	public MessageBuilder(NotifyBuilder notifyBuilder) {
		this.notifyBuilder = notifyBuilder;
	}

	/**
	 * Build a JSON string that should be sent off to rollbar. We delegate to the wrapped notifierbuilder,
	 * then override some values. We also need to prevent the string from exceeding 100kb, the max
	 * size of a task queue task. Mostly this is a concern for StackOverflowErrors.
	 */
	public String buildJson(String level, String message, Throwable throwable, Map<String, String> context) {
		final JSONObject json = notifyBuilder.build(level, message, throwable, context);

		json.put("uuid", UUID.randomUUID());

		JSONObject notifier = new JSONObject();
		notifier.put("name", "rollbar-appengine");
		notifier.put("version", NOTIFIER_VERSION);
		json.put("notifier", notifier);

		truncateExcessiveStackFrames(json);

		return json.toString();
	}

	/**
	 * Recursively look for 'frames' and truncate any beyond an unreasonable #.
	 * This JSON library is ghastly.
	 */
	private void truncateExcessiveStackFrames(Object json) {
		if (json instanceof JSONObject) {
			for (String key : (Set<String>)((JSONObject)json).keySet()) {
				if (key.equals("frames")) {
					JSONArray framesArray = ((JSONObject)json).optJSONArray(key);
					if (framesArray != null) {
						for (int i = framesArray.length(); i > MAX_STACK_FRAMES; i--) {
							framesArray.remove(i-1);
						}
					}
				} else {
					truncateExcessiveStackFrames(((JSONObject)json).get(key));
				}
			}
		} else if (json instanceof JSONArray) {
			for (int i = 0; i < ((JSONArray)json).length(); i++) {
				truncateExcessiveStackFrames(((JSONArray)json).get(i));
			}
		}
	}
}
