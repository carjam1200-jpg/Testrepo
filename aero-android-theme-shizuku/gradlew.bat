@echo off
setlocal
set APP_HOME=%~dp0
set DIST_VERSION=8.9
set GRADLE_HOME=%APP_HOME%.gradle-wrapper\gradle-%DIST_VERSION%
if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  echo Please run the project through Android Studio or generate a standard Gradle wrapper locally.
  exit /b 1
)
call "%GRADLE_HOME%\bin\gradle.bat" %*
