package net.lakis.cerebro.jobs.async.standalone;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true,fluent = true)
class AsyncHolder<T> {

	private T response;
	private boolean responded;
	private boolean timedout;

	 

}
