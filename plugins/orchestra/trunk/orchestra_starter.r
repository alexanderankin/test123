#!/bin/env Rscript
# {{{ Copyright/licence notice
# :tabSize=2:indentSize=2:noTabs=false:folding=explicit:collapseFolds=1:
# Copyright (c) 2009, Romain Francois <francoisromain@free.fr>
# Copyright (c) 2009, Bernd Bischl <bernd_bischl@gmx.net>
#
# This file is part of the orchestra jedit plugin
#
# The orchestra jedit plugin is free software:
# you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 2 of the License, or
# (at your option) any later version.
#
# The orchestra jedit plugin is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with the orchestra jedit plugin. If not, see <http://www.gnu.org/licenses/>.
# }}}

# {{{ read the property file
# the directory that contains that script
args <- commandArgs( ) 
thisfile <- sub( "--file=", "", grep( "^--file", args, value = TRUE )[1] )
propfile <- file.path( dirname( thisfile ), "orchestra_properties.txt" )
if( !file.exists( propfile ) ){
	stop( "the property file does not exist" ) 
}
rl    <- readLines( propfile )
if( any( grepl( "@", rl ) ) ){
	stop( "the properties are not configured properly" )
}
source( propfile )
# }}}

# {{{ write log to plugin home
sink(file=file.path(PLUGIN_HOME, "orchestra_starter.log"), split=TRUE)
# }}}

# {{{ environment variables
SEP <- .Platform$path.sep

# {{{ R_HOME
R_HOME <- R.home()
Sys.setenv(R_HOME = R_HOME )
# }}}

# {{{ add the plugin home to R_LIBS
R_LIBS <- Sys.getenv( "R_LIBS", unset = "" )
JEDIT_RLIBS <- file.path( PLUGIN_HOME, "library" )
# set by java orchestra installer plugin
R_LIBS <- if( R_LIBS == "" ){
	JEDIT_RLIBS
} else{
	paste( JEDIT_RLIBS, R_LIBS, sep = SEP )
}
Sys.setenv( R_LIBS = R_LIBS )
# }}}

# {{{ make sure orchestra R package is installed somewhere
orchestra.installed <- function( libpath ){
	file.exists( file.path( libpath, "orchestra" ) )
}
if( !any( sapply( .libPaths(), orchestra.installed ) ) ){
	if( !orchestra.installed( JEDIT_RLIBS ) ){
		cat( sprintf( 'the orchestra package is not installed, try installing it like this :\nR> install.packages("orchestra", dependencies = TRUE, lib = "%s")\n', JEDIT_RLIBS))
		stop()
	}
}
# }}}

# {{{ JAVA_HOME and java
# set by java orchestra installer plugin, so we always know java_home
java <- file.path( JAVA_HOME, "bin", java_exe )
if( !file.exists( java ) ){
	stop(paste("no", java_exe, "in bin dir of", JAVA_HOME))
}
# }}}

# {{{ JEDIT_HOME and jedit.jar
# set by java orchestra installer plugin, so we always know jedit_home
jedit.jar <- file.path( JEDIT_HOME, "jedit.jar" )
if( !file.exists( jedit.jar ) ){
	stop( "the jedit home directory does not contain a file called jedit.jar" )
}
# }}}

# {{{ PATH
Sys.setenv( PATH =
	paste(
		file.path( R.home(), "bin") ,
		file.path( JAVA_HOME, "bin" ) ,
		Sys.getenv('PATH'),
	sep= SEP) )
# }}}

# {{{ LD_LIBRARY_PATH
LD_LIBRARY_PATH <- Sys.getenv( "LD_LIBRARY_PATH", unset = "" )
if( LD_LIBRARY_PATH == "" ){
	LD_LIBRARY_PATH <- Sys.getenv( "JAVA_LIBRARY_PATH", unset = "" )
}
# add JRI
JRI_PATH <- system.file( "jri", package = "rJava" )
JRI_LD_PATH <- paste( JRI_PATH, file.path( R_HOME, "lib"), sep = SEP )
Sys.setenv( JRI_PATH = JRI_PATH )

LD_LIBRARY_PATH <- if( LD_LIBRARY_PATH == "" ){
	JRI_LD_PATH
} else {
	paste( LD_LIBRARY_PATH, JRI_LD_PATH, sep = SEP )
}
Sys.setenv( LD_LIBRARY_PATH = LD_LIBRARY_PATH )
# }}}

# {{{ ORCHESTRA_HOME
# add our lib dir to r lib paths so we find the (possible) local installation of orchestra package
.libPaths(JEDIT_RLIBS)
ORCHESTRA_HOME = system.file( package = "orchestra" )
# }}}
# }}}

# {{{ the command
cmd <- sprintf( '"%s" -Djava.library.path="%s" -Dorchestra.home="%s" -Drhome="%s" -jar "%s" ',
	java, LD_LIBRARY_PATH, ORCHESTRA_HOME, R_HOME, jedit.jar )

# additional parameters
params <- commandArgs( TRUE )
if( length( params ) ){
	cmd <- paste( cmd, params, sep = " ")
}

# run asynchronously to suppress shell - at least necessary on windows 
system(cmd, wait=FALSE)

# }}}

