/*
h4Jmf.java
program is free software GNU General Public License
author Herman Vierendeels,Ternat,Belgium
*/
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.EventQueue;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;

import be.dekamer.programs.allerlei.Console;

public class h4Jmf extends JPanel
implements h4JmfActions,EBComponent
{
 private static final Logger logger=Logger.getLogger(h4Jmf.class.getName());
 private static String sdfs="HH:mm:ss.SS";
 private static SimpleDateFormat sdf=new SimpleDateFormat(sdfs);
 volatile private static int count=0;
 volatile private static java.awt.Component cpc=null;
 volatile private static Player playMP3=null;

 private String filename=null;
 private View view;
 Console cnsl=null;
 //java.awt.Component cpc=null;

 private URL url;
 private MediaLocator mediaLocator;
 private DataSource ds=null;
 //nog doen??? if commando (play, start etc) , set switch, unset switch when event confirmed in controllerUpdate
 // no more commands if command_busy set!!! nog doen???
 //private Player playMP3;
 //private Time lsbr_time=null;
 private Time duration=null;
 private Calendar clndr=Calendar.getInstance();

 boolean jmf_ok=false;

 public h4Jmf()
 {
  logger.info("no-args constructor");
 }//

 public h4Jmf(View view,String position)
 {
  super();
  count++;
  logger.info("args constructor "+count);

  this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
  //logger.info("view="+view+" position="+position);
  this.view=view;
  //position.equals(DockableWindowManager.FLOATING);
  cnsl=new Console(100);
  //cnsl.setPreferredSize(new Dimension(500,200));
  JScrollPane jscrllpn=new JScrollPane(cnsl);
  //jscrllpn.setPreferredSize(new Dimension(500,200));
  //add(BorderLayout.SOUTH,jscrllpn);
  add(jscrllpn);
  logger.info("cpc="+cpc);
  if(cpc!=null)//after panel has been closed, new instance !!
  {
   cpc.setPreferredSize(new Dimension(500,50));
   this.add(cpc);
  }
  this.setPreferredSize(new Dimension(500,250));
  //cnsl.append("begin");
  //cnsl.append("SettingsDirectory="+jEdit.getSettingsDirectory());
  cnsl.append("args constructor "+count);
  //EditBus.addToBus(this);//moved to notufy
  //check a few jmf-classes begin
  Class tst=null;
  try
  {
   tst=Class.forName("com.sun.media.codec.audio.mp3.JavaDecoder");
   jmf_ok=true;
   cnsl.append("jmf Java Media Framework seems to be installed.");
  }
  catch(Exception excptn)
  {
   cnsl.append("trying presence of jmf");
   cnsl.append(excptn);
   cnsl.append("jmf Java Media Framework does not seem to be installed!");
   cnsl.append("see README for more info");
  }
  //check a few jmf-classes end
  logger.info("constructor end");
 }//constructor


 public void chooseFile()
 {
  if(! jmf_ok) return;

  String tmpdir=System.getProperty("java.io.tmpdir");
  String[] paths=GUIUtilities.showVFSFileDialog(view,tmpdir+File.separator,JFileChooser.OPEN_DIALOG,false);
  if(paths!=null && !paths[0].equals(filename))
  {
   filename=paths[0];
   cnsl.append("filename="+filename);
   try
   {
    this.url=new URL("file://"+filename);
    player_begin();
   }
   catch(Exception e)
   {
    logger.severe(e.getMessage());
   }
   //readFile();
  }
 }//chooseFile

 public void player_begin()
 {
  if(playMP3!=null)
  {
   logger.severe("playMP3!=null");
   cnsl.append("playMP3!=null");
   return;
  }
  if(url==null)
  {
   cnsl.append("url==null");
   return;
  }
  mediaLocator=new MediaLocator(url); 
  try
  {
   final JPanel jpnl_this=this;

   ds=Manager.createDataSource(mediaLocator);
   //cnsl.append("ds="+ds);
   playMP3=Manager.createPlayer(ds);
   playMP3.addControllerListener(new ControllerListener()
    {
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
       playMP3.stop();
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
       stmp=sdf.format(clndr.getTime());
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
           cpc=playMP3.getControlPanelComponent();
           //logger.info("cpc="+cpc);
           if(cpc!=null)
           {
            //jpnl_this.add(BorderLayout.NORTH,cpc);
            jpnl_this.add(cpc);
	    //jpnl_this.invalidate();
	    //getHeight() getWidth() setSize(Dimension).setSize(int width,int height)
	    jpnl_this.revalidate();
           }
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
  }
  catch(Exception e)
  {
   logger.severe(e.getMessage());
   cnsl.append(e);
   return;
  }
  playMP3.realize();
  logger.info("after realize()"); 
  //but possible [JMF thread: com.sun.media.PlaybackEngine@1ac13d7[ com.sun.media.PlaybackEngine@1ac13d7 ] ( realizeThread)] [error] PlaybackEngine@1ac13d7 ] ( realizeThread):   Unable to handle format: mpeglayer3, 16000.0 Hz, 16-bit, Mono, LittleEndian, Signed, 2000.0 frame rate, FrameSize=16384 bits
  //running tshvr under hedwig :11:17:08 PM [JMF thread: com.sun.media.content.unknown.Handler@8c7be5 ( prefetchThread)] [error] Handler@8c7be5 ( prefetchThread): Error: Unable to prefetch com.sun.media.PlaybackEngine@6d3b92
 }//player_begin
 public void player_close()
 {
  cnsl.append("trying to close()");
  if(playMP3!=null)
  {
   playMP3.close();
   cnsl.append("close()");
   if(cpc!=null) this.remove(cpc);
  }
  cpc=null;
  playMP3=null;
  filename=null;
 }//
 //nog doen!!! start forward rewind , kleine delay inbouwen ???
 public void player_start()
 {
  if(playMP3==null) return;
  playMP3.start();
 }//player_start
 public void player_stop()
 {
  if(playMP3==null) return;
  playMP3.stop();
 }//player_stop
 public void player_forward()
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
  //tmp=playMP3.getMediaTime();
  //cnsl.append("MediaTime set to:"+tmp.getSeconds());
 }//player_forward
 public void player_rewind()
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
  double seconds=tmp.getSeconds()-x;
  if(seconds<=0) seconds=0.5;//???? 
  javax.media.Time newTime=new javax.media.Time(seconds);
  playMP3.setMediaTime(newTime);
  tmp=playMP3.getMediaTime();
  //cnsl.append("MediaTime set to:"+tmp.getSeconds());
 }//player_rewind
 public void player_position()
 {
  if(playMP3==null) return;
  View view_tmp=jEdit.getActiveView();
  JEditTextArea txta=view.getEditPane().getTextArea();
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
  stmp=stmp.substring(posb+2,pose);
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
 public void insert_time()
 {
  if(playMP3==null) return;
  View view_tmp=jEdit.getActiveView();
  //logger.info("view_tmp="+view_tmp.toString());
  JEditTextArea txta=view.getEditPane().getTextArea();
  int crtp=txta.getCaretPosition();
  JEditBuffer bffr=txta.getBuffer();
  Time time_tmp=playMP3.getMediaTime();
  double ms=time_tmp.getSeconds()*1000;
  int millis=(int)ms;
  clndr.clear();
  clndr.set(Calendar.MILLISECOND,0);
  clndr.add(Calendar.MILLISECOND,millis);
  String stmp=sdf.format(clndr.getTime());
  //nog doen << >> in sdfs, sdfs in opties ???
  bffr.insert(crtp,"<<"+stmp+">>");
 }
 //EBComponent interface
 public void handleMessage(EBMessage message)
 {
  logger.info(message.toString());
  if(message instanceof PropertiesChanged)
  {
   //propertiesChanged();
  }
  //AWT-EventQueue-0: INFO: EditorExiting[source=null]
  else if(message instanceof org.gjt.sp.jedit.msg.PluginUpdate)
  {
   Object what=((PluginUpdate)message).getWhat();
   if(what.equals(PluginUpdate.DEACTIVATED))
   {
    if(playMP3!=null) playMP3.close();
    playMP3=null;
   }
  }//PluginUpdate
 }//handleMessage
 //JComponent methods
 /****if using these without call to super,view empty! no cnsl, no cpc*/
 public void addNotify()
 {
  logger.info("");
  super.addNotify();
  EditBus.addToBus(this);
 }
 public void removeNotify()
 {
  logger.info("");
  super.removeNotify();
  EditBus.removeFromBus(this);
 }
 //JComponent methods end
}//h4Jmf
/************
DockableWindowUpdate[what=ACTIVATED,dockable=h4Jmf,source=org.gjt.sp.jedit.gui.DockableWindowManagerImpl[,0,0,1024x669,layout=org.gjt.sp.jedit.gui.DockableLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=9,maximumSize=,minimumSize=,preferredSize=]]

EditPaneUpdate[what=DESTROYED,source=org.gjt.sp.jedit.EditPane[active,editpane]]

PluginUpdate[what=UNLOADED,exit=true,source=/downloads/jEdit/4.3pre16/jars/LatestVersion.jar,class=LatestVersionPlugin]


 javax.media.NotRealizedError: Cannot get control panel component on an unrealized player
**************/
