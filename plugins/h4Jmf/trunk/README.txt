h4Jmf plugin for jEdit java editor
Herman Vierendeels,Ternat,Belgium

to assist in transcription of digital audio-interview.Pause, back-step, forward-step , position from cue, insert cue in text, play functions with shortcuts from editor-window

install Java Media Framework JMF
 http://java.sun.com/javase/technologies/desktop/media/jmf/
 http://java.sun.com/javase/technologies/desktop/media/jmf/2.1.1/apidocs/
 http://java.sun.com/products/java-media/jmf/2.1.1/setup.html

 linux:
  download jmf-2_1_1e-linux-i586.bin from http://java.sun.com/javase/technologies/desktop/media/jmf/2.1.1/download.html
  execute /bin/sh ./jmf-2_1_1e-linux-i586.bin
  this last command adapts $HOME/.jmfdir
  either jmf.jar should be in your jEdit CLASSPATH
  or should be copied to $JAVA_HOME/jre/lib/ext/

 download javamp3-1_0.zip from http://java.sun.com/javase/technologies/desktop/media/jmf/mp3/download.html
  this file contains mp3plugin.jar.
  mp3plugin.jar should be copied to $JAVA_HOME/jre/lib/ext

works under linux, not yet tested under windows

sourceforge:
https://sourceforge.net/projects/jedit/
https://sourceforge.net/project/memberlist.php?group_id=588
jedit-Plugin Central Submission-2786053
https://sourceforge.net/tracker/?func=detail&atid=625093&aid=2786053&group_id=588
jedit-devel@lists.sourceforge.net
jedit-cvs@lists.sourceforge.net
http://jedit.svn.sourceforge.net/viewvc/jedit/
https://sourceforge.net/scm/?type=svn&group_id=588

problems
--------
 [JMF thread: SendEventQueue: com.sun.media.content.unknown.Handler] [error] Handler: INFO: e=javax.media.ResourceUnavailableEvent[source=com.sun.media.content.unknown.Handler@4845aa,message=Failed to prefetch: cannot open the audio device.]
   under linux , /sbin/fuser -v /dev/snd/*

 java.lang.NoClassDefFoundError: javax/media/ControllerListener
  jmf not installed 
 
 [JMF thread: SendEventQueue: com.sun.media.content.unknown.Handler] [error] Handler: INFO: e=javax.media.ResourceUnavailableEvent[source=com.sun.media.content.unknown.Handler@3bc20e,message=Failed to realize: input media not supported: mpeglayer3 audio]
 check
  important (at least under linux):
   $HOME/.jmfdir should contain good location of jmf jar's and lib's.
    should have been set by installing jmf
   $JAVA_HOME/jre/lib/ext/ should contain mp3plugin.jar,jmf.jar
