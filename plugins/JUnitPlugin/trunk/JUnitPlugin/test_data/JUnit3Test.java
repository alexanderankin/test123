
import junit.framework.*;
import static junit.framework.Assert.*;

/**
 */
public class JUnit3Test extends TestCase {

	public void testSucceeding1(){
		System.out.println("JUnit3Test.testSucceeding1()");
	}

	public void testSucceeding2(){
		System.out.println("JUnit3Test.testSucceeding2()");
	}

	public void testFailing(){
		assertTrue("Junit3Test.testFailing",false);
	}
	
	public void testError(){
		throw new RuntimeException("JUnit3Test.testError()");
	}
	
	public void notATest(){
		throw new UnsupportedOperationException("Don't call notATest !");
	}
}
