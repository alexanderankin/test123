JCompiler - A java compiler plugin for jEdit
============================================

INTRODUCTION

JCompiler uses the Java compiler from the JDK library, written in Java.
The compiler will be started in the same VM that jEdit runs in. This is the
fastest way to start the compiler delivered with the JDK, because no time is
wasted to start a new VM.


INTERNALS

Here's what the compiler class looks like:


    package sun.tools.javac;
    public class Main {
        public Main(OutputStream output, String product) { ... }
        public boolean compile(String[] arguments) { ... }
    }


So what JCompiler does is the following:

    1) Look for this class and load it.
    2) Construct a new instance with the Console as OutputStream.
       The product string is always "javac".
    3) Invoke the compile() method with the specified arguments.

The class usually is located in lib/tools.jar. JCompiler tries to do best
to find this library automatically, but sometimes it could fail. In this
case, please read the JCompiler help page.

Please note the following:

  * On older JDK's (JDK 1.1.x) the class is located in classes.zip.
    JCompiler should find this class automatically.
  * Java Runtime Environments (JRE's) don't include the compiler.
    You need a full Java Development Kit (JDK) to use JCompiler.


CONTACT

If you have any questions or suggestions, please inform me:
Dirk Moebius <dmoebius@gmx.net>

Or ask the jEdit Users mailing list. Subscription information can be found
on the jEdit homepage: http://jedit.sourceforge.net

