#
# make a list grow until crash
#
#
from os import *
import sys,token,symbol
#import mysubsample


#
# A Big List test 
#
def buildList():
  
    "test documentation comment"
    print "this will return a sample constant list back"
    list=["dave" , "mark" , "ann" , "phil"]
    count = 0 ;
    #try :
    while 1 :
        
      list.append("this string will be appended until we got a crash")
      count = count +1
      if count % 10000 == 0 :
        print count , "element inserted"
    #except MemoryError :
    #  list = None 
    #  print "we run outof Memory"

if __name__ == '__main__':
#   checking for provided parameters
  buildList()
