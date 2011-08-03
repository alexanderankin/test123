#!/usr/bin/env python
from distutils.core import setup

setup( name="jpydaemon" , 
       version="0.18" ,
       description="jpydbg python debugger backend + samples" ,
       author="Jean-Yves Mengant" ,
       author_email="jymengant@ifrance.com" ,
       url="http://jpydbg.sourceforge.net"  ,
       py_modules=["jpydbg/jpydaemon" , "jpydbg/firstsample" , "jpydbg/inspector" , "jpydbg/completion" ,
                                  "jpydbg/dbgutils " ,"jpydbg/pylintlauncher",  "jpydbg/simplejy" , "jpydbg/simplepy" ]
     )

