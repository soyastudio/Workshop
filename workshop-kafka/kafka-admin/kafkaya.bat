@echo off

rem Set environment variables
set AES_ROOT=%~sdp0..\
set AES_PID_DIR=%AES_ROOT%\run
set AES_LOG_DIR=%AES_ROOT%\logs

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
goto startDecrypt

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly.
echo Instead the PATH will be used to find the java executable.
echo.
set JAVA_EXE=java
goto startDecrypt

:startDecrypt
set LIB_DIR=%AES_ROOT%\lib

SET JAVA_PARAMS=-cp %LIB_DIR%\* -Xms12m -Xmx24m %JAVA_ARGS% com.albertsons.esed.aes.AES
set BOOTSTRAP_ACTION=run

cmd.exe /C "%JAVA_EXE%" %JAVA_PARAMS% %*

popd
