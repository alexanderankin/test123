@echo off

setlocal

set ANT_HOME=X:\softref\tools\ant\v-v1.3
set JUNIT_JAR=X:\softref\tools\junit\junit-v3.5.jar

set ANT_CMD=%ANT_HOME%\bin\ant.bat
if NOT EXIST %ANT_CMD% goto noAntCmd

if NOT EXIST %JUNIT_JAR% goto noJunitJar
set CLASSPATH=%JUNIT_JAR%


%ANT_CMD% -Djunit.jar=%JUNIT_JAR% %*
goto fini


:noAntCmd
echo Cannot find ant batch file at: %ANT_CMD%
goto fini

:noJunitJar
echo Cannot find junit jar file at: %JUNIT_JAR%

goto fini

:fini
