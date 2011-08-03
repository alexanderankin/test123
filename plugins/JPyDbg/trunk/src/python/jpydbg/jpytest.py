#
# this utility is used by jpy dbg either in pytrhon   
# or jython to set up the PYTHONPATH debuging environment
#
import sys
import os.path

#
# Unit testing startup
#
if __name__ == '__main__':
#   checking for provided parameters
  print "args = " , sys.argv
  print "sys.path =", sys.path
  pass
