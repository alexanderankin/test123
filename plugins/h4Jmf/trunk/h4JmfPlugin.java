/*
h4Jmf.java
program is free software GNU General Public License
author Herman Vierendeels,Ternat,Belgium
*/

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;


import javax.media.*;

import be.dekamer.programs.allerlei.Console;


public class h4JmfPlugin extends EditPlugin
{
 private static final Logger logger=Logger.getLogger(h4JmfPlugin.class.getName());

 volatile protected static boolean jmf_ok=false;
 volatile public static Player playMP3=null;
 protected static Calendar clndr=Calendar.getInstance();
 private static String sdfs="<<HH:mm:ss.SS>>";
 protected static SimpleDateFormat sdf=new SimpleDateFormat(sdfs);
 protected static Console cnsl=new Console(100);

 public static final String NAME="h4Jmf";
 public static final String OPTION_PREFIX="options.h4Jmf.";

 public h4JmfPlugin()
 {
  logger.info("no-args constructor");
  logger.info("trying presence of JMF");
  Class tst=null;
  try
  {
   tst=Class.forName("com.sun.media.codec.audio.mp3.JavaDecoder");
   logger.info("JMF seems to be there");
   jmf_ok=true;
  }
  catch(Exception excptn)
  {
   logger.severe(excptn.toString());
   logger.warning("JMF Java Media Framework does not seem to be installed!");
   logger.info("see README for more info");
   /////!!!throw(new Error("JMF Java Media Framework not installed"));
  }
 }//


 public void start()
 {
  logger.info("start plugin");
  //new Throwable().printStackTrace();
  //
 }//start
 public void stop()
 {
  logger.info("stop plugin");
  if(playMP3!=null)
  {
   logger.info("trying player close()");
   playMP3.close();
   playMP3=null;
  }
 }//stop
 //player methods, were before in h4Jmf.java
 //
 /*********************************/
 protected static void player_close()
 {
  if(playMP3==null) return;
  playMP3.close();
  playMP3=null;
 }//
 protected static void player_begin()
 {
  cnsl.append("plugin player_begin begin");
  try
  {
   Object cl=(ControllerListener)Class.forName("h4JmfControllerListener").newInstance();
   //h4JmfControllerListener cl=new h4JmfControllerListener();
   playMP3.addControllerListener((ControllerListener)cl);
   playMP3.realize();
   logger.info("after realize()"); 
  /************************************************************
   h4JmfPlugin.playMP3.addControllerListener(new ControllerListener()
    {
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
       duration=((DurationUpdateEvent)e).getDuration();
       //cnsl.append("duration="+duration.getSeconds());
       double ms=duration.getSeconds()*1000;
       //cnsl.append("ms="+ms);
       int millis=(int)ms;
       //cnsl.append("millis="+millis);
       clndr.clear();
       clndr.set(Calendar.MILLISECOND,0);
       clndr.add(Calendar.MILLISECOND,millis);
       stmp=h4JmfPlugin.sdf.format(clndr.getTime());
       cnsl.append("duration="+stmp);
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
           if(h4JmfPlugin.playMP3!=null)
           {	  
            java.awt.Component cpc=h4JmfPlugin.playMP3.getControlPanelComponent();
            //logger.info("cpc="+cpc);
            if(cpc!=null)//after panel has been closed, new instance !!
            {
             jpnl_this.add(cpc);
	     //getHeight() getWidth() setSize(Dimension).setSize(int width,int height)
	     jpnl_this.revalidate();
            }
           }//
          }//run
         }
        );
       }//gui_thread
      }//RealizeCompleteEvent
      else if(e instanceof javax.media.ResourceUnavailableEvent)
      {
       cnsl.append(((javax.media.ResourceUnavailableEvent)e).getMessage());
      }
     }//controllerUpdate
    }
   );
   ************************************/
  }
  catch(Throwable e)
  {
   logger.severe(e.getMessage());
   cnsl.append(e);
  }
  //but possible [JMF thread: com.sun.media.PlaybackEngine@1ac13d7[ com.sun.media.PlaybackEngine@1ac13d7 ] ( realizeThread)] [error] PlaybackEngine@1ac13d7 ] ( realizeThread):   Unable to handle format: mpeglayer3, 16000.0 Hz, 16-bit, Mono, LittleEndian, Signed, 2000.0 frame rate, FrameSize=16384 bits
  //running tshvr under hedwig :11:17:08 PM [JMF thread: com.sun.media.content.unknown.Handler@8c7be5 ( prefetchThread)] [error] Handler@8c7be5 ( prefetchThread): Error: Unable to prefetch com.sun.media.PlaybackEngine@6d3b92
 }//player_begin
 /*********************************/

 public static void player_start()
 {
  if(playMP3==null) return;
  playMP3.start();
 }//player_start
 public static void player_stop()
 {
  if(playMP3==null) return;
  playMP3.stop();
 }//player_stop
 public static void player_forward()
 {
  int x=5;
  if(playMP3==null) return;
  String stmp=jEdit.getProperty(h4JmfPlugin.OPTION_PREFIX+"seconds");
  //logger.info("stmp="+stmp);
  if(stmp!=null && stmp.length()>0)
  {
   try
   {
    x=Integer.parseInt(stmp);
   }
   catch(Exception excptn)
   {
    logger.severe("stmp="+stmp+" "+excptn.toString());
   }
  }//stmp
  Time tmp=playMP3.getMediaTime();
  double seconds=tmp.getSeconds()+x;
  //if(seconds<=0) seconds=1;//???? 
  javax.media.Time newTime=new javax.media.Time(seconds);
  playMP3.setMediaTime(newTime);
 }//player_forward
 public static void player_rewind()
 {
  int x=5;
  if(playMP3==null) return;
  String stmp=jEdit.getProperty(h4JmfPlugin.OPTION_PREFIX+"seconds");
  //logger.info("stmp="+stmp);
  if(stmp!=null && stmp.length()>0)
  {
   try
   {
    x=Integer.parseInt(stmp);
   }
   catch(Exception excptn)
   {
    logger.severe("stmp="+stmp+" "+excptn.toString());
   }
  }//stmp
  Time tmp=h4JmfPlugin.playMP3.getMediaTime();
  double seconds=tmp.getSeconds()-x;
  if(seconds<=0) seconds=0.5;//???? 
  javax.media.Time newTime=new javax.media.Time(seconds);
  playMP3.setMediaTime(newTime);
 }//player_rewind
 public static void insert_time()
 {
  if(playMP3==null) return;
  View view_tmp=jEdit.getActiveView();
  //logger.info("view_tmp="+view_tmp.toString());
  JEditTextArea txta=view_tmp.getEditPane().getTextArea();
  int crtp=txta.getCaretPosition();
  JEditBuffer bffr=txta.getBuffer();
  Time time_tmp=playMP3.getMediaTime();
  double ms=time_tmp.getSeconds()*1000;
  int millis=(int)ms;
  clndr.clear();
  clndr.set(Calendar.MILLISECOND,0);
  clndr.add(Calendar.MILLISECOND,millis);
  String stmp=sdf.format(clndr.getTime());
  //bffr.insert(crtp,"<<"+stmp+">>");
  bffr.insert(crtp,stmp);
 }
 public static void player_position()
 {
  if(playMP3==null) return;
  View view_tmp=jEdit.getActiveView();
  JEditTextArea txta=view_tmp.getEditPane().getTextArea();
  int crtp=txta.getCaretPosition();
  JEditBuffer bffr=txta.getBuffer();
  int start=crtp-17;
  int end=bffr.getLength();
  if(start<0) start=0;
  int maxlen=end-start;
  int len=17+17;
  if(len>maxlen) len=maxlen;
  String stmp=bffr.getText(start,len);
  int posb=stmp.indexOf("<<");
  if(posb<0) return;
  int pose=stmp.indexOf(">>",posb+2);
  if(pose<0) return;
  //logger.info("posb="+posb+" pose="+pose);
  //stmp=stmp.substring(posb+2,pose);
  stmp=stmp.substring(posb,pose+2);
  //cnsl.append("stmp="+stmp);
  Date dt_tmp=null;
  try
  {
   dt_tmp=sdf.parse(stmp);
  }
  catch(Exception excptn)
  {
   logger.severe(excptn.toString());
   return;
  }
  //cnsl.append("dt_tmp="+dt_tmp);
  clndr.clear();
  clndr.setTime(dt_tmp);
  clndr.set(Calendar.ERA,0);
  clndr.set(Calendar.YEAR,0);
  clndr.set(Calendar.MONTH,0);
  clndr.set(Calendar.DAY_OF_MONTH,0);
  //cnsl.append("clndr="+clndr);
  //double seconds=0;
  double ltmp=0;
  //millis=clndr.getTimeInMillis();
  //cnsl.append("millis="+millis);
  ltmp=clndr.get(Calendar.HOUR_OF_DAY)*60*60;
  //cnsl.append("ltmp="+ltmp);
  ltmp=ltmp+clndr.get(Calendar.MINUTE)*60;
  //cnsl.append("ltmp="+ltmp);
  ltmp=ltmp+clndr.get(Calendar.SECOND);
  //cnsl.append("ltmp="+ltmp);
  javax.media.Time newTime=new javax.media.Time(ltmp);
  playMP3.setMediaTime(newTime);
  //cnsl.append(""+playMP3.getMediaTime().getSeconds());
 }//
}//h4JmfPlugin
/**************
stacktrace at start plugin
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0: INFO: start plugin
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0: java.lang.Throwable
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at h4JmfPlugin.start(Unknown Source)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.PluginJAR.startPlugin(PluginJAR.java:1363)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.PluginJAR.activatePlugin(PluginJAR.java:739)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.JARClassLoader._loadClass(JARClassLoader.java:347)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.JARClassLoader.loadClass(JARClassLoader.java:108)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at java.lang.ClassLoader.loadClass(ClassLoader.java:251)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.classpath.ClassManagerImpl.classForName(ClassManagerImpl.java:203)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.NameSpace.classForName(NameSpace.java:1318)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.NameSpace.getClassImpl(NameSpace.java:1218)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.NameSpace.getClass(NameSpace.java:1159)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.Name.consumeNextObjectField(Name.java:298)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.Name.toObject(Name.java:199)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.BSHAmbiguousName.toObject(BSHAmbiguousName.java:59)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.BSHAllocationExpression.objectAllocation(BSHAllocationExpression.java:86)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.BSHAllocationExpression.eval(BSHAllocationExpression.java:62)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.BSHPrimaryExpression.eval(BSHPrimaryExpression.java:102)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.BSHPrimaryExpression.eval(BSHPrimaryExpression.java:47)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.Interpreter.eval(Interpreter.java:644)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.Interpreter.eval(Interpreter.java:738)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.bsh.Interpreter.eval(Interpreter.java:727)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.BeanShellFacade._eval(BeanShellFacade.java:148)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.BeanShellFacade.eval(BeanShellFacade.java:113)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.BeanShell.eval(BeanShell.java:387)
10:39:21 [AWT-EventQueue-0] [error] AWT-EventQueue-0:  at org.gjt.sp.jedit.gui.OptionsDialog.valueChanged(OptionsDialog.java:204)
***************/
