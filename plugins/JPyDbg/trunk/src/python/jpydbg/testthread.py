import threading
import thread
import time
import bdb
import sys
import inspect
import types

class DeepThread(threading.Thread):

	def __init__(self):
		threading.Thread.__init__(self)
		self.setName("Deep thread")
	
	def f1(self, first):
		print "entering f1 with argument ", first 
		time.sleep(1)
		self.f2(first, "and one more")
		print "leaving f1"
    
	def f2(self, one, two):
		print "entering f2", one, two
		for i in range(100):
			self.f3(one, two, 3)
		print "leaving f2"
    
	def f3(self, ein, tzwei, drei):
		print "entering f3"
		print ein
		print tzwei
		print str(drei)
		print "one"
		print "two"
		print "three"
		print "four"
		print "five"
		print "six is sleep"
		time.sleep(1)
		print "seven"
		print "eight"
		print "leaving f3"

	def run(self):
		self.f1("hello")
		
class First(threading.Thread):
	def __init__(self, delay, name):
		threading.Thread.__init__(self)
		self.delay = delay
		self.setName(name)
		self.setDaemon(True)
		self.__verbose = True

	def run(self):
		i=4
		while (i > 0):
			i = i - 1
			print self.getName() + str(i)
			time.sleep(self.delay)
		print >>sys.stderr, "exiting", self.getName()

class KillEm(threading.Thread):
	def __init__(self, delay):
		threading.Thread.__init__(self)
		self.delay = delay
		self.setName("KillerThread")

	def run(self):
		print "coming in for a kill in " + str(self.delay) + " seconds"
		time.sleep(self.delay)
		print "Killing!" + str(self.delay) + " seconds"
		Tdb.instance.do_quit()
		
class Tdb:
	instance = None
	
	def __init__(self):
		Tdb.instance = self
		threading.settrace(self.trace_dispatch) # for all threads

	def trace_dispatch(self, frame, event, arg):
		print frame
		print id(frame)
		
		if event == 'line':
			return self.dispatch_line(frame)
		if event == 'call':
			return self.dispatch_call(frame, arg)
		if event == 'return':
			return self.dispatch_return(frame, arg)
		if event == 'exception':
			return self.dispatch_exception(frame, arg)
		return self.trace_dispatch

	def dispatch_line(self, frame):
#		print id(threading.currentThread())
#		print inspect.getframeinfo(frame)
		return self.trace_dispatch

	def dispatch_call(self, frame, arg):
		print "C " + str(frame) + " " + str(arg)
		return self.trace_dispatch

	def dispatch_return(self, frame, arg):
		print "R " + str(frame) + " " + str(arg)
		return self.trace_dispatch

	def dispatch_exception(self, frame, arg):
		print "E " + str(frame) + " " + str(arg)
		return self.trace_dispatch

	def run(self, cmd, globals=None, locals=None):	
		if globals is None:
			import __main__
			globals = __main__.__dict__
		if locals is None:
			locals = globals

		if not isinstance(cmd, types.CodeType):
			cmd = cmd+'\n'

		try:
			try:
				exec cmd in globals, locals
			except:
#				print sys.stderr, "Debugger exiting with exception"
				raise
		finally:
			print "Quitting now"
			self.quitting = 1

def infinite_fun():
	i = 1
	while True:
		i += 10
		time.sleep(i)
		print "I've been asleep for " + str(i) + " sec."



def infinite2():
	infinite_fun()
	
def infinite_1():
	infinite2()
	
		
def many_threads():
	print "many threads"
	t = First(1, "1 seconder ")
	t2 = First(2, "2 cool 4 you ")
	t3 = First(3, "3 is a magic number ")
	dt = DeepThread()
        print "before t start" 
	t.start()
        print "before t2 start" 
	t2.start()
        print "before t3 start" 
	t3.start()
        print "before dt start" 
	dt.start()
        print "last line of many threads" 
#	infinite_1()


def test():
	t = Tdb()
	t.run('many_threads()')

if __name__ == "__main__":
	many_threads()
        print "leaving ........ "
        
	raw_input("ready to quit now? ")