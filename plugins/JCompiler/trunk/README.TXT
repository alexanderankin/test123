JCompiler - A java compiler plugin for jEdit
============================================

REQUIREMENTS

- jEdit >= 2.6pre7
- plugin EditBus >= 0.9.4
- plugin ErrorList >= 0.11
- plugin Console >= 2.2
- a Java Development Kit (JDK) >= 1.1, NOT a Java Runtime Environment (JRE)!

Note: the plugins ErrorList and Console are not required directly. JCompiler
should start without them. But without them you won't see anything. 


INSTALLATION

1. Use jEdit's PluginManager to install the JCompiler plugin, or:

   Unzip the contents of JCompiler.zip into the directory <jedithome>/jars:

        $ cd <jedithome>
        $ cd jars
        $ unzip JCompiler.jar
        
   Depending on your OS and your JDK, here's the location of <jedithome>:
   
   Operating system    JDK                  Directory <jedithome>
   ------------------- -------------------- --------------------------------
   Windows 95,98,ME    SUN JDK, IBM JDK     C:\WINDOWS\.jedit
                       Microsoft JDK        C:\WINDOWS\JAVA\.jedit
   Windows NT          all                  C:\WINNT\profiles\<user>\.jedit
   Unix                all                  $HOME/.jedit
   Macintosh           ???                  ??? (please inform me)

   
2. If you have an JDK 1.1, you're ready. If you have an JDK 1.2 or higher
   (ie. a JDK for Java2), you must perform an additional installation step:

   Make sure, that the file tools.jar from the JDK lib directory is in your
   classpath. This can be done in two ways:
   
   a) Copy <jdkdir>/lib/tools.jar to <jedithome>/jars, or
   b) Set the environment variable CLASSPATH to contain tools.jar, ie:
      on Unix:
           export $CLASSPATH=$CLASSPATH:<jdkdir>/lib/tools.jar
      on Windows:
           set CLASSPATH %CLASSPATH%;<jdkdir>\lib\tools.jar


DEVELOPERS

Attention! If you want to compile the source yourself, compile the file
jcompiler/NoExitSecurityManager2.java with a JDK1.2 javac, _not_ a
JDK 1.1 javac.

The compiled code run will run on both JDK's nevertheless, because on a
JDK 1.1, NoExitSecurityManager2 is never invoked.


SUPPORT

If you have any questions or suggestions, please inform me: 
Dirk Moebius <dmoebius@gmx.net>

Or ask the jEdit Users mailing list. Subscription information can be found
on the jEdit homepage: http://jedit.sourceforge.net

