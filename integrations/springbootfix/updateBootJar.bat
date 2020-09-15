@echo off
@break off
@title Create folder with batch but only if it doesn't already exist - D3F4ULT
@color 0a
@cls
set jar="%java_home%\bin\jar.exe"
set java="%java_home%\bin\java.exe"

Echo Launch dir: "%~dp0"
Echo Current dir: "%CD%"
set extractDir=%~dp0extract\
if not exist %~dp0extract (
    echo Folder does not exist %extractDir% extracting classes to patch
    mkdir "%extractDir%"
    cd %extractDir%
    %jar% -xvf ..\fluxtion-api-2.6.3-SNAPSHOT.jar
    %jar% -xvf ..\fluxtion-streaming-api-2.6.3-SNAPSHOT.jar
    %jar% -xvf ..\fluxtion-text-api-2.6.3-SNAPSHOT.jar
    %jar% -xvf ..\lombok-1.18.12.jar
    %jar% -xvf ..\disruptor-3.4.2.jar
    cd "%CD%"  
) 

echo patching lombok classes
%jar% uf %~dp0../target/fluxtion-integration-2.6.3-SNAPSHOT.jar  -C %extractDir% lombok
timeout /t 2
echo patching fluxtion classes
%jar% uf %~dp0../target/fluxtion-integration-2.6.3-SNAPSHOT.jar  -C %extractDir% com
echo completed parching 
