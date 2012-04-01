package com.kpouer.jedit.smartopen;

import org.gjt.sp.jedit.View;
/**
  @author Alan Ezust
 */

public class FileOpener extends org.jedit.core.FileOpenerService {
	public void openFile(String fileName, View view) {
		SmartOpenPlugin.smartOpenDialog(view, fileName);
	}
}

