import socket
import dbgutils
import sys
# import rlcompleter
import string
import inspect
import os


HOST = '' 
PORT = 29001 # default listening port
UNKNOWN = -1
NOT_FOUND = "No suggestions"


# instanciate a jpyutil object
_utils = dbgutils.jpyutils()

class Introspector:
    """ 
    completion introspection module 
      """
    def __init__( self , candidate , recurse = True , parent = None ) :
        # evaluate to introspect
        self._candidate = eval( candidate )
        self._parentClass = None
        self.children = None
        self._name = candidate
        # reduced name is the final part of full name
        self._reducedName = candidate.split('.')[-1]
        # self._doc  = inspect.getdoc(self._candidate)
        self._args = None
        self._type = '5'
        self._startline = None
        self._endline   = None
        self._sourceFile   = None
        self._varargs = None
        self._varkw = None
        if self._candidate != None :
            if inspect.ismodule(self._candidate) or  \
               inspect.isfunction(self._candidate) or \
               inspect.ismethod(self._candidate) or \
               inspect.isclass(self._candidate) or \
               inspect.istraceback(self._candidate) or \
               inspect.isframe(self._candidate) :
                try :   
                    lines = inspect.getsourcelines(self._candidate)
                    self._source = lines[0]
                    self._startline = lines[1]
                    self._endline   = lines[1] + len(lines[0])
                except :
                    pass # ignore default values have been set
            if inspect.ismodule(self._candidate) :
                self._type = '0'
                if  recurse :
                    self.children = self._processChildren(inspect.getmembers( self._candidate ))
                try :     
                    self._sourceFile = inspect.getsourcefile( self._candidate )
                except :
                    pass # ignore default values have been set
            elif inspect.isclass(self._candidate) :
                self._type = '1'
                if recurse :
                    self.children = self._processChildren(inspect.getmembers( self._candidate))
            elif inspect.isfunction(self._candidate) or \
                 inspect.ismethod(self._candidate) :
                if inspect.ismethod(self._candidate) :
                    func = self._candidate.im_func
                else :
                    func = self._candidate
                if inspect.isfunction(func) :
                    myArgs = inspect.getargspec(func)
                    self._args = myArgs[0]
                    self._varargs = myArgs[1]
                    self._varkw = myArgs[2]
                    self._locals = myArgs[3]
                
                self._type = '2'
                if inspect.ismethod(self._candidate) :
                    # get parent class inspector info
                    wk = candidate.split('.')
                    if parent == None :
                        self._parentClass = Introspector( '.'.join( wk[:len(wk)-1] ) , False )
                    else :
                        self._parentClass = parent
            elif inspect.isroutine(self._candidate) :
                self._type = '2'
            
    def _processChildren( self , children ) :
        """ process dependencies recursivelly """
        returned = []
        for child in children :
            returned.append( Introspector(self._name +'.'+child[0] , False , self ) )
        return returned

    def getName( self ) :
        return self._name
        
    def isSelf( self , location ) :
        if self._type == '1' and \
           self._startline <= location and \
           self._endline >= location :
            return True
        return False
        
    def __str__( self  ):
        """ return easy to parse string image """
        if  self._type == '0' :
            returned = "@@MODULE@@{"
        elif  self._type == '1' :
            returned = "@@CLASS@@{"
        elif  self._type == '2' :
            returned = "@@FUNCTION@@{"
        else  :
            returned = "@@VARIABLE@@{"

        returned = ''.join([ returned , "'name':'" , self._reducedName , "'," ])
        # doc may be large do'nt process it now
        # if self._doc != None :
        #    returned = ''.join([ returned , "'doc':'" , self._doc, "'," ])
        if  self._type == '2' :
            strArgs= ''
            sep = ''  
            if self._args != None :
                for arg in self._args :
                    strArgs = sep.join( [ strArgs , arg ] )
                    sep = ','
            else :
                strArgs = 'None' 
            returned = ''.join([ returned , "'args':'" , strArgs , "'," ])
            returned = ''.join([ returned , "'varargs':'" , str(self._varargs) , "'," ])
            returned = ''.join([ returned , "'varkw':'" , str(self._varkw) , "'," ])
        
        returned = ''.join([ returned , "'startline':'" , str(self._startline) , "'," ])
        returned = ''.join([ returned , "'endline':'" , str(self._endline) , "'" ])
        
        if self.children != None :
            returned = ''.join([ returned , ",'children':["])
            concat = ''
            for child in self.children :
                returned = concat.join( [ returned , str(child) ] )
                concat = ','
            returned = ''.join([ returned , "]"])
        returned = ''.join([ returned , "}"])
        return returned


class Completion :
    """ Autocompetion Factory  """
    
    def __init__( self , candidate = None ) :
        # self._completer = rlcompleter.Completer()
        self._modName = candidate
        self._importError = None
        # refresh loaded module or bring it in memory if new
        try :
            mod = sys.modules.get(self._modName)
            if mod != None :
                reload(mod)
            else :
                myGlobs = globals()
                myGlobs[self._modName] = __import__(self._modName)
        except ImportError :
            self._importError = ' '.join( [self._modName , "can't be loaded : " + str(sys.path) ] )   
        self._lastContext = None

    def __filter( self , toComplete , recurse = True  ) :
        """ filter completion with available data list """
        completed = None
            # parent = datas.locate(completion)
        try  :
            if len(toComplete) != 0 :
                return  Introspector(toComplete , recurse )
        except ( NameError,AttributeError,SyntaxError) :
            return None
        return completed
        
    def complete( self , toComplete , location  ):
        """ handle completion on provided toComplete string and line location """
        # nearest = self.getLocalContext( location )
        # deals with self case
        if self._importError != None :
            return self._importError
        if toComplete[-1] == '.' :
            toComplete = toComplete[:-1]
        spl = toComplete.split('.')
        if spl[0] == 'self' :
            wk = Introspector( self._modName )
            if wk.children != None :
                for child in wk.children :
                    if ( child.isSelf( location ) ) :
                        spl[0] = child.getName()
                        break
                if spl[0] == 'self' :
                    return None # not inside a class => no way to complete we can leave
            toComplete = '.'.join(spl) # finally rebuild string
        returned = self.__filter( toComplete  )
        if returned == None :
            # try using current module namespace
            returned = self.__filter( self._modName + '.' + toComplete )
        if returned == None :
            return NOT_FOUND
        # recurse back to module level
        hierarch = returned._name.split('.')
        while len( hierarch ) > 1 :
            del hierarch[-1]
            parent = self.__filter( '.'.join(hierarch) , False )
            parent.children = [ returned ]
            returned = parent
            hierarch = returned._name.split('.')
        return str(returned).replace('\n',' ')

class CompletionDaemon :
    
    def __init__( self ) :
        self._connection = None 
        # allocate a default completion engine
        self._completion = None
        self.cmd = None

    # rough command/subcommand syntax analyzer    
    def commandSyntax( self , command ):
        self.cmd  = UNKNOWN
        verb , arg  = _utils.nextArg(command)
        return verb , arg  
    
    def importSource( self , source ):
        """ import candidate source to have access to symbols """
        # first isolate path from file
        fName = os.path.basename(source).split('.')[0]
        pathName = os.path.dirname(source)
        sys.path.insert(0,pathName)
        
        self._completion = Completion( fName )
        
    def dealWithComplete( self , arg ):
        source , arg       = _utils.nextArg(arg)
        line  , tocomplete  = _utils.nextArg(arg)
        # check for current completion source first
        self.importSource( source  )
        # finally handle completion
        return self._completion.complete(tocomplete , int(line) )
        
    def parseCommand( self , command ):
        """ parse provided completion command """
        verb , arg = self.commandSyntax( command )
        if verb == None :
            return 0 
        if ( string.upper(verb) == "COMPLETE" ):
            self._connection.populateToClient( [ self.dealWithComplete( arg ) , '\n' ] )
        else:
            self._connection.populateToClient( [ command ,  "= SYNTAX ERROR"  , '\n'])
        return 1
        
    def start( self , myport = PORT  ):
        """ start completion daemon """
        # start in listen mode waiting for incoming sollicitors   
        s = socket.socket( socket.AF_INET , socket.SOCK_STREAM )
        s.bind( (HOST , myport) )
        s.listen(1)
        print "Completion listening on " , myport 
        connection , addr = s.accept()
        self._connection = dbgutils.NetworkSession(connection)
        print "connected by " , addr
        welcome = [ 'WELCOME' , '\n' ]
        self._connection.populateToClient( welcome )
        while ( self.parseCommand( self._connection.receiveCommand() ) ):
            pass    
          
        print "'+++ Completion/sessionended/"
        self._connection.close()
 
    
# start a listening completion engine instance when invoked as main program
# without arguments
if __name__ == "__main__":
    instance = CompletionDaemon()
    print "args = " , sys.argv
    # test a change
    # returned = instance.dealWithComplete("D:\\sourceforgecvs\\jpydebugforge\\src\\python\\jpydbg\\inspector.py 363 self.")
    # returned = instance.dealWithComplete("D:\\sourceforgecvs\\jpydebugforge\\src\\python\\jpydbg\\inspector.py 363 sys.")
    # returned = instance.dealWithComplete("D:\\sourceforgecvs\\jpydebugforge\\src\\python\\jpydbg\\firstsample.py 23 socket.")
    # returned = instance.dealWithComplete("D:\\sourceforgecvs\\jpydebugforge\\src\\python\\jpydbg\\firstsample.py 23 os.error.")
    # print returned
    port = _utils.consumeArgv()
    if port == None:
        port = PORT
    else:
        port = int(port)
    # second argument is PATHFILE location
    pyPathArg = _utils.consumeArgv()
    if (pyPathArg != None ):
        pythonPath = dbgutils.PythonPathHandler(pyPathArg)
        pythonPath.getPyPathFromFile()
    # finally start the completion dameon instance
    #
    instance.start( myport=port ) 
    print "deamon ended\n"
