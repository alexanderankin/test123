import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 */
public class SpinningTest{

	@Test
	public void infiniteDepth(){
		infiniteDepth();
	}

	@Test
	public void loopingTest(){
		long l = 0;
		for(int i=0;i<Integer.MAX_VALUE;i++){
			l+= i;
		}
		System.err.println("done loopingTest "+l);
	}

}
