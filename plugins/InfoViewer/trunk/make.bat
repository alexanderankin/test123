@echo off

if "%1" == "" goto all
goto %1

:all
javac -g *.java infoviewer\*.java infoviewer\actions\*.java infoviewer\workaround\*.java
goto end

:jar
jar cv0Mf ../InfoViewer.jar *.class *.html *.txt *.props infoviewer
goto end

:clean
del *.class
del infoviewer\*.class
del infoviewer\actions\*.class
del infoviewer\workaround\*.class
del ..\InfoViewer.jar
goto end

:end
