import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 */
public class JUnit4Test{

	@Test
	public void succeedingTest1(){
		System.out.println("JUnit4Test.succeedingTest1()");
	}

	@Test
	public void succeedingTest2(){
		System.out.println("JUnit4Test.succeedingTest2()");
	}

	@Test
	public void failingTest(){
		assertTrue("Junit4Test.failingTest",false);
	}
	
	@Test
	public void assumeTest(){
		assumeTrue(false);
		assertTrue("not triggered",false);
	}

	@Test @Ignore
	public void ignoredTest(){
		System.out.println("JUnit4Test.ignoredTest()");
	}
	
	@Test
	public void errorTest(){
		throw new RuntimeException("JUnit4Test.errorTest()");
	}
}
