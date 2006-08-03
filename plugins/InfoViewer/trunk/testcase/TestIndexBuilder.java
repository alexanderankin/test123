package testcase;

import infoviewer.lucene.IndexBuilder;

import java.lang.reflect.Method;


import junit.framework.TestCase;

import org.gjt.sp.jedit.jEdit;



public class TestIndexBuilder extends TestCase
{
	IndexBuilder indexBuilder;
	
	protected void setUp() throws Exception
	{
		indexBuilder = IndexBuilder.instance();
		

	}
    
	public void testSettingsDir() {
		String settingsDir= jEdit.getSettingsDirectory();
		assertEquals("/home/ezust/.jedit", settingsDir);
		indexBuilder.search("fold");
	}

	
}
