#
#  zope python startup
#  based on runzope.bat to easyly debug a zope session
#
import sys

if __name__ == '__main__':
  ZOPE_HOME="f:\\Zope-2.7.0\\"
  SOFTWARE_HOME= ZOPE_HOME + "\\lib\\python"
  sys.path.insert(0, SOFTWARE_HOME )
  import Zope , ZPublisher
  
  ZPublisher.Zope('')
#  Zope.Startup.run.run()
 
  
