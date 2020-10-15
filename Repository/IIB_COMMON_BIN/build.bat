REM temporary create drive V for file transfer
subst V: %1

REM Copy source codes
robocopy V:\src\ %1 /E

subst V: /d

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_162
set ANT_HOME=C:\Program Files\Apache
set IIB_HOME=C:\Program Files\IBM\IIB\10.0.0.13\server\bin

call "%IIB_HOME%\mqsiprofile.cmd"

call "%ANT_HOME%\bin\ant" -v -f %1\bin\build.xml -Dworkspacedir=%1