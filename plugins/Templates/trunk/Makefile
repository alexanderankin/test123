# Makefile for "test" module
# This makefile is designed for use with Unix make

.SUFFIXES: .java .class
.java.class:
	@echo Compiling $<:
#	@echo $(CLASSPATH)
	@$(JAVAC) -classpath $(CLASSPATH):$(deps) $<

JAVA=java
JAVAC=javac
JAR=jar
PROJHOME=/home/sjakob/projects
deps=/share/jedit-2.4pre6/jedit.jar

classes=TemplatesPlugin.class \
		TemplatesAction.class \
		TemplateFile.class \
		TemplateDir.class \
		TemplatesOptionPane.class \
		TemplateVar.class \
		Template.class
others= Templates.props \
		Templates.html
indist=	README.txt \
		CHANGES.txt \
		makefile.jmk \
		samples/gnu-gpl.txt \
		samples/Java/class.java \
		samples/C++/class.cpp
		

all: $(classes)
#	-javadoc -author -noindex -notree -d doc ca.on.london.sjakob.test
	@jar c0vf Templates.jar \
		$(classes) $(others)

clean:
	rm -f ./*.class
	rm -f ./*.*~
	rm -f ./*.jar
#	rm -f doc/*.htm*

install:
	rm -f $(HOME)/.jedit/jars/Templates.jar
	cp Templates.jar $(HOME)/.jedit/jars

dist:
	rm -rf Templates/*
	cp *.java Templates
	cp $(others) Templates
	cp -P $(indist) Templates
	zip -r Templates.zip Templates/*

