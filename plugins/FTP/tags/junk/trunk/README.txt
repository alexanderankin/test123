This document details how to build a newer jsch.jar file that is a dependency 
of the FTP plugin.

1) The last generated jar is in lib/jsch-<VERSION>.jar

2) Download jsch source (>=0.1.32) http://www.jcraft.com/jsch/

3) Download jzlib source http://www.jcraft.com/jzlib/

4) Compile into .class files, create jar called jsch-<VERSION>.jar

5) copy into lib/, and add to subversion



