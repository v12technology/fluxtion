@echo off
@break off
@title Create folder with batch but only if it doesn't already exist 
@color 0a
@cls
set jar="%java_home%\bin\jar.exe"
set java="%java_home%\bin\java.exe"
set version=%1

Echo Launch dir  : "%~dp0"
Echo Current dir : "%CD%"
echo version     : "%version%"

set extractDir=%~dp0extract\
if not exist %~dp0extract (
    echo Folder does not exist %extractDir% extracting classes to patch
    mkdir "%extractDir%"
    cd %extractDir%
    %jar% -xvf ..\..\..\api\target\fluxtion-api-%version%.jar
    %jar% -xvf ..\..\..\extensions\streaming\api\target\fluxtion-streaming-api-%version%.jar
    %jar% -xvf ..\..\..\extensions\text\api\target\fluxtion-text-api-%version%.jar
    %jar% -xvf ..\disruptor-3.4.2.jar
    cd "%CD%"  
) 

echo patching fluxtion classes
%jar% uf %~dp0../target/fluxtion-etl-%version%.jar  -C %extractDir% com
echo completed patching 
