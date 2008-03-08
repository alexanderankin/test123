import junit.framework.TestCase;

public class ChildTest extends TestCase {
	
	public void testEnsureDefaults() {
		Child child = new Child();
		assertEquals("default", child.getName());
		assertEquals(-100, child.getAge());
	}

}
