
#enter the name of your project
NAME       = ProjectViewer

#Select your Java Compiler
JAVAC         = jikes
#JAVAC        = /usr/local/jdk1.2.2/bin/javac

#tell it your base project
BASE       = projectviewer

#tell make where to place debug files
DEBUGDIR = /usr/local/jedit-2.2.1/jars/

################################################################################
# You should not need to change anything below here.
################################################################################

#turn on debugging for BETA version
#JAVACOPTS    = -O -g:none
JAVACOPTS    = -g
BINDIR       = ../

PACKAGES  = \
	$(BASE) \

MANIFEST   = MANIFEST
LOG        = make.log

DISTDIR  = ./bin
DISTBASEFILE = $(NAME)
DISTFILE = $(DISTBASEFILE).tar.gz

JAR          = jar
DOCDIR       = ./docs/api
JAVADOC      = javadoc
JAVADOCOPTS  = -d $(DOCDIR) -author -version -private

WINDOWTITLE = """API Specification"""
BOTTOM      = ""
STYLESHEET  = "../docs/stylesheet.css"

DIRECTORIES   = . $(subst .,/,$(PACKAGES))
BASEDIRECTORY = $(subst .,/,$(BASE))
ALLSOURCES    = $(foreach dir,$(DIRECTORIES),$(wildcard $(dir)/*.java))
ALLCLASSES    = $(foreach dir,$(DIRECTORIES),$(dir)/*.class)
ALLHTML       = $(foreach dir,$(DIRECTORIES),$(wildcard $(dir)/*.html))
ALLGIFS       = $(foreach dir,$(DIRECTORIES),$(wildcard $(dir)/*.gif))
ALLPROPS      = $(foreach dir,$(DIRECTORIES),$(wildcard $(dir)/*.props))


.PHONY : jar javadoc clean dist

default:
	@echo
	@echo building...
	@echo compiling $(NAME) using "$(JAVAC) $(JAVACOPTS)"
	@$(JAVAC) $(JAVACOPTS) $(ALLSOURCES) 2> $(LOG)

jar: default
	@echo
	@echo Creating $(NAME).jar package...
	@test -d $(DISTDIR) || mkdir $(DISTDIR)
	@echo building package $(NAME).jar
	@rm -f $(DISTDIR)/$(NAME).jar
	@$(JAR) cf $(DISTDIR)/$(NAME).jar $(ALLCLASSES) $(ALLPROPS) $(ALLHTML) $(ALLGIFS) ./icons/*

javadoc:
	@echo
	@echo Building API documentation using "$(JAVADOC) $(JAVADOCOPTS)"
	@test -d $(DOCDIR) || mkdir $(DOCDIR)
	@echo creating the docs using $(JAVADOC)
	@$(JAVADOC) $(JAVADOCOPTS) $(PACKAGES) 2>> $(LOG)


clean:
	@echo
	@echo Cleaning up the distribution...
	@rm -f $(ALLCLASSES) 2> $(LOG)
	@rm -rf temp $(LOG) $(DOCDIR) $(BINDIR)/$(NAME).jar *.jar
	@rm -rf $(DOCDIR)

debug: jar
	@echo Moving $(NAME).jar to $(DEBUGDIR)
	@cp $(DISTDIR)/$(NAME).jar $(DEBUGDIR)

publish: dist
	@echo 
	@echo Publishing file
	@scp $(DISTDIR)/$(DISTBASEFILE).tar.gz root@relativity.yi.org:/usr/home/httpd/wwwroot/jEditDocs/plugins


dist: clean jar javadoc
	@echo
	@test -d $(DISTDIR) || mkdir $(DISTDIR)
	@echo Creating the $(NAME) distribution...
	@echo Creating the $(NAME) distribution as $(DISTFILE)
	@rm -f $(DISTDIR)/*.tar.gz
	@tar c * > $(DISTDIR)/$(DISTBASEFILE).tar;gzip -c $(DISTDIR)/$(DISTBASEFILE).tar > $(DISTDIR)/$(DISTFILE);rm $(DISTDIR)/$(DISTBASEFILE).tar
	@echo
	@echo Done.

test:
	@echo NAME:		$(NAME)
	@echo BASE: 		$(BASE)
	@echo BINDIR:		$(BINDIR)
	@echo LOG:		$(LOG)
	@echo JAR:		$(JAR)
	@echo JAVADOC:		$(JAVADOC)
	@echo DIRECTORIES:	$(DIRECTORIES)
	@echo ALLSOURCES:	$(ALLSOURCES)
	@echo ALLCLASSES:	$(ALLCLASSES)


