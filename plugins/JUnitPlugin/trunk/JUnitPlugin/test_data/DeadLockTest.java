import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * blocks without consuming CPU
 */
public class DeadLockTest{

	@Test
	public void deadLock() throws InterruptedException{
		final Object lock = new Object();
		synchronized(lock){
			Thread t = new Thread(){
				public void run(){
					synchronized(lock){
						System.err.println("hello");
					}
				}
			};
			t.start();
			t.join(10000);
		}
	}

	/** 2nd test, will be skipped */
	@Test
	public void loopingTest(){
		long l = 0;
		for(int i=0;i<Integer.MAX_VALUE;i++){
			l+= i;
		}
		System.err.println("done loopingTest "+l);
	}

}
