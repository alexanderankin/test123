#
# This sample python file may be used to check jpydbg 
# installation and configuration correctness
#
__revision__ = '$Revision: 1.4 $'
__date__ ='$Date: 2006/11/25 15:25:27 $'
# $Source: /cvsroot/jpydbg/jpydebugforge/src/python/jpydbg/firstsample.py,v $

print 'sourceSample loaded name=' ,__name__ 
#from os import *
import sys
import os
import socket
import codecs

#
# import mysubsample

class Titi :
    '''
    test single ml 
    '''
    def __init__( self , a ) :
        ''' test single '''
        returned = 0
        self._reducedName = 0
        returned = ''.join([ returned , "'name':'" , self._reducedName , "'," ])
        pass
        
    def newmethod( self , kiki ) :
        pass
        
class Tata :
    def __init__( self ):
        """ test """
        pass
#
# A sample List returning function test modified again
#
def buildList():
    """
    test documentation comment
  """ 
    print "this will return a sample constant list back"
    mylist = ["dave" , "mark" , "ann" , "phil"]
    return mylist
#
# A sample multiplication function
#
def multiply( first , second  ):
    print "this shall add " , first , "times " , second
    returned = 0
    for i in range ( 0, long(first) ):
        returned = returned + long(second)
    print i
    return returned

#
# Unit testing startup
#
if __name__ == '__main__':
#   checking for provided parameters
    args = sys.argv
    print "length args=" , len(args)
#    a = raw_input("ready to quit now? ")
#    print "a received = " , a
    print "next line entered"
    if len( args ) > 1 :
        ii = 0 
        for current in args:
            print "arg [" , ii , "]=" , str(args[ii])
            ii = ii + 1
    else:
        print "no argument provided"
# check writeline case on stdout capture
#    mystdout = sys.stdout
# TODO: test todo
#    mystdout.writeline("test writeline implemented in jpydbg")
# test KeyError correct capture on Dict
    print "should really not stop here"
    print 'should stop here'
    print "Hello","World!"
    print "Hello" + ' ' + "World!"
    myvar = "test"
    data = {'last':"Feigenbaum", 'first':"Barry"}
    print data
    myDict = { 'spam':2 , 'zozo':1 }
    try :
        print myDict['riri']
    except KeyError:
        print "we got it "
#
    print "testing multiply = " , multiply ("5" , "6")
    for ii in xrange(10):
        print "ii value is = " , ii 
    print "testing multiply = " , multiply (5, 6)
#    print "testing multiply = " , multiply (5,6,7)
#   test uncaptured exception on next line
    print "testing buildlist = " , buildList() 
#    1+''
#    print "strange : we should not be here"
#   print mysubsample.testImporter("zozo")

