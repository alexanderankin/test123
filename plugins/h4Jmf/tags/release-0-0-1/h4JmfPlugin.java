/*
h4Jmf.java
program is free software GNU General Public License
author Herman Vierendeels,Ternat,Belgium
*/
import java.util.logging.*;

import org.gjt.sp.jedit.EditPlugin;

public class h4JmfPlugin extends EditPlugin
{
 private static final Logger logger=Logger.getLogger(h4JmfPlugin.class.getName());

 public static final String NAME="h4Jmf";
 public static final String OPTION_PREFIX="options.h4Jmf.";

 public void start()
 {
  logger.info("start plugin");
 }//start
 public void stop()
 {
  logger.info("stop plugin");
 }//stop
}
