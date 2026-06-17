@echo off
setlocal
set "APP_HOME=%~dp0"
java "%APP_HOME%gradle\wrapper\GradleBootstrap.java" "%APP_HOME%" %*
exit /b %ERRORLEVEL%
