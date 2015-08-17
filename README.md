# Rollbar For Google App Engine

The provided java Rollbar libraries are tied to log4j or logback and don't work on Google App Engine. This library
works on GAE by wrapping and overriding behavior in [tapstream's rollbar-logback](https://github.com/tapstream/rollbar-logback)
library.
 
The library provides a filter; any exceptions which pass up through this filter will be sent to Rollbar as an error.
Unlike the logback or log4j libraries, this library does not send arbitrary log messages to Rollbar - that does not
appear to be possible with Google's logging system. However, this is "good enough" for most purposes. 

## Source code

https://github.com/stickfigure/rollbar-appengine

## Download

This plugin is available in Maven Central:

	<dependency>
		<groupId>com.voodoodyne.rollbar</groupId>
		<artifactId>rollbar-appengine</artifactId>
		<version>please look up latest version</version>
	</dependency>

It can be downloaded directly from [http://search.maven.org/]

## Usage

If you're using Guice, bind a `NotifyBuilder` and a `Queue`:

	@Provides
	public NotifyBuilder notifyBuilder() throws UnknownHostException {
		return new NotifyBuilder("your server access token", "production", null);
	}
	
	@Provides @Rollbar	// note the qualifier
	public Queue queue() {
		return QueueFactory.getQueue("rollbar");
	}
	
	
If you are not using Guice, subclass `AppengineRollbarFilter` and give it a default constructor:

	public class MyRollbarFilter extends AppengineRollebarFilter {
		public MyRollbarFilter() {
			super(
				new MessageBuilder(new NotifyBuilder("your server access token", "production", null)),
				QueueFactory.getDefaultQueue()
			);
		}
	}
	
Then install the filter. Any exceptions which bubble up through this filter will be sent to Rollbar.

## Author

* Jeff Schnitzer (jeff@infohazard.org)

## License

This software is provided under the [MIT license](http://opensource.org/licenses/MIT)
