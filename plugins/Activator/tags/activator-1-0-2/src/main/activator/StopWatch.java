package activator;

/**
 *  Description of the Class
 *
 * @author     mace0031
 * @created    February 25, 2004
 * @version    $Revision$
 * @updated    $Date$ by $Author$
 */
public class StopWatch {
	//{{{ private fields
	private long started;
	private long stopped = 0;

	private boolean running;
	private int runs = 0;
	//}}}

	/**
	 *  Start the stopwatch.
	 *
	 * @throws  IllegalStateException  if the stopwatch is running.
	 */
	public void start() {
		if (running) {
			throw new IllegalStateException("Already started");
		}
		//reset both start and stop
		started = System.currentTimeMillis();
		stopped = 0;
		running = true;
		runs++;
	}

	/**
	 *  Stop the stopwatch.
	 *
	 * @throws  IllegalStateException  if the stopwatch isn't running.
	 */
	public void stop() {
		if (!running) {
			throw new IllegalStateException("Stopwatch isn't running");
		}
		stopped = System.currentTimeMillis();
		running = false;
	}

	public long value() {
		checkState();
		return stopped-started;
	}

	public String toString() {
		checkState();
		return (stopped-started)+" ms";
	}

	private void checkState() {
		if (runs == 0)
			throw new IllegalStateException("Stopwatch hasn't been run");
	}

}

