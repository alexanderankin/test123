This document details how to build the jsch.jar file that is a dependency 
of the FTP plugin.


1) Download jsch source (>=0.1.32) http://www.jcraft.com/jsch/

2) Download jzlib source http://www.jcraft.com/jzlib/

3) Unpack both source trees and copy the jzlib tree into the jsch tree.

4) Run ant in the jsch directory with the flag: -Djzlib.available

5) The jar is in [jsch]/dist/lib/jsch-<DATE>.jar

