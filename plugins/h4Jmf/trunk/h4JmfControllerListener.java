/*
h4Jmf.java
program is free software GNU General Public License
author Herman Vierendeels,Ternat,Belgium
*/
import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.gui.*;


import javax.media.*;

public class h4JmfControllerListener
implements javax.media.ControllerListener
{
 private static final Logger logger=Logger.getLogger(h4JmfControllerListener.class.getName());

 //this controller class is there, even if h4Jmf-JPanel closed!
 public void controllerUpdate(ControllerEvent e)
 {
  logger.info("e="+e);	    
  //[JMF thread: SendEventQueue: com.sun.media.content.unknown.Handler] [error] Handler: INFO: e=javax.media.RestartingEvent[source=com.sun.media.content.unknown.Handler@164bff9,previous=Started,current=Prefetched,target=Started,mediaTime=javax.media.Time@74187a] ???
  //[JMF thread: SendEventQueue: com.sun.media.content.unknown.Handler] [error] Handler: INFO: e=javax.media.ControllerClosedEvent[source=com.sun.media.content.unknown.Handler@1f195fc]
  //e=javax.media.TransitionEvent[source=com.sun.media.content.unknown.Handler@1dd3812,previous=Unrealized,current=Realizing,target=Realized]
  //[JMF thread: SendEventQueue: com.sun.media.content.unknown.Handler] [error] Handler: INFO: e=javax.media.ResourceUnavailableEvent[source=com.sun.media.content.unknown.Handler@c2cf83,message=Failed to realize: input media not supported: mpeglayer3 audio]
  //[JMF thread: SendEventQueue: com.sun.media.content.unknown.Handler] [error] Handler: INFO: e=javax.media.ResourceUnavailableEvent[source=com.sun.media.content.unknown.Handler@19050a0,message=Failed to prefetch: cannot open the audio device.]

  if(e instanceof EndOfMediaEvent)
  {
   h4JmfPlugin.playMP3.stop();
  }//EndOfMediaEvent
  else if(e instanceof StopByRequestEvent)
  {
   //lsbr_time=((StopByRequestEvent)e).getMediaTime();
   //cnsl.append("lsbr_time="+lsbr_time.getSeconds());
  }
  else if(e instanceof DurationUpdateEvent)
  {
   String stmp=null;
   Time duration=((DurationUpdateEvent)e).getDuration();
   h4JmfPlugin.cnsl.append("duration="+duration.getSeconds());
   double ms=duration.getSeconds()*1000;
   h4JmfPlugin.cnsl.append("ms="+ms);
   int millis=(int)ms;
   h4JmfPlugin.cnsl.append("millis="+millis);
   h4JmfPlugin.clndr.clear();
   h4JmfPlugin.clndr.set(Calendar.MILLISECOND,0);
   h4JmfPlugin.clndr.add(Calendar.MILLISECOND,millis);
   stmp=h4JmfPlugin.sdf.format(h4JmfPlugin.clndr.getTime());
   h4JmfPlugin.cnsl.append("duration="+stmp);
  }
  else if(e instanceof RealizeCompleteEvent)
  {
   boolean is_gui_thread=java.awt.EventQueue.isDispatchThread();
   //logger.info("is_gui_thread="+is_gui_thread);
   if(! is_gui_thread)
   {
    EventQueue.invokeLater(new Runnable()
     {
      public void run()
      {
       logger.info("h4JmfControllerListener runnable run() begin");
       if(h4JmfPlugin.playMP3!=null)
       {	  
        java.awt.Component cpc=h4JmfPlugin.playMP3.getControlPanelComponent();
        //logger.info("cpc="+cpc);
        if(cpc!=null)//after panel has been closed, new instance !!
        {
	 //String[] rdws=DockableWindowManager.getRegisteredDockableWindows();
	 //for(int l=0;l<rdws.length;l++) logger.info("rdws "+l+"="+rdws[l]);
	 //INFO: rdws 3=h4Jmf
	 View[] vws=jEdit.getViews();
	 for(int l=0;l<vws.length;l++)
	 {
	  logger.info("vws "+l+"="+vws[l]);
	  DockableWindowManager dwm=vws[l].getDockableWindowManager();
	  javax.swing.JComponent jcmpnt=dwm.getDockable(h4JmfPlugin.NAME);
	  if(jcmpnt!=null)
	  {
           logger.info("jcmpnt="+jcmpnt.getClass().getName());
           logger.info("jcmpnt="+jcmpnt);
	   //jcmpnt=h4Jmf[,0,13,595x235,invalid,layout=javax.swing.BoxLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=9,maximumSize=,minimumSize=,preferredSize=java.awt.Dimension[width=500,height=300]]
	   jcmpnt.add(cpc);
	   jcmpnt.revalidate();
	  }
	 }//for
        }//cpc
       }//
      }//run
     }
    );
   }//gui_thread
  }//RealizeCompleteEvent
  else if(e instanceof javax.media.ResourceUnavailableEvent)
  {
   h4JmfPlugin.cnsl.append(((javax.media.ResourceUnavailableEvent)e).getMessage());
   logger.severe(((javax.media.ResourceUnavailableEvent)e).getMessage());
  }
 }//controllerUpdate
}//h4JmfControllerListener
