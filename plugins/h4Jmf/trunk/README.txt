h4Jmf plugin for jEdit java editor
Herman Vierendeels,Ternat,Belgium

install Java Media Framework JMF
http://java.sun.com/javase/technologies/desktop/media/jmf/
http://java.sun.com/javase/technologies/desktop/media/jmf/2.1.1/apidocs/
http://java.sun.com/products/java-media/jmf/2.1.1/setup.html
http://java.sun.com/javase/technologies/desktop/media/jmf/mp3/download.html
important (at least under linux):
 $HOME/.jmfdir should contain location of jmf jar's and lib's.
  should have been set by calling: java  JMFRegistry   (in jmf.jar)
 $JAVA_HOME/jre/lib/ext/ should contain mp3plugin.jar,jmf.jar

to assist in transcription of digital audio-interview.Pause, back-step, forward-step , position from cue, insert cue in text, play functions with shortcuts from editor-window


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
[JMF thread: SendEventQueue: com.sun.media.content.unknown.Handler] [error] Handler: INFO: e=javax.media.ResourceUnavailableEvent[source=com.sun.media.content.unknown.Handler@4845aa,message=Failed to prefetch: cannot open the audio device.]
 under linux , /sbin/fuser -v /dev/snd/*
