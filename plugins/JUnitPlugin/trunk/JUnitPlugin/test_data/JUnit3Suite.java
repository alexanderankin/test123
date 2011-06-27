
import junit.framework.*;
import static junit.framework.Assert.*;

/**
 */
public class JUnit3Suite extends TestCase {

	public void testSucceeding1(){
		System.out.println("JUnit3Suite.testSucceeding1()");
	}

	public void testSucceeding2(){
		System.out.println("JUnit3Suite.testSucceeding2()");
	}

	public void testFailing(){
		assertTrue("JUnit3Suite.testFailing",false);
	}
	
	public void testError(){
		throw new RuntimeException("JUnit3Suite.testError()");
	}
	
	public void notATest(){
		throw new UnsupportedOperationException("Don't call notATest !");
	}
	
	public static Test suite() {
     TestSuite suite= new TestSuite();
      suite.addTest(new JUnit3Test());
      suite.addTest(new JUnit3Suite());
      return suite;
  }
}
