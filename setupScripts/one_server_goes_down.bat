@echo off
echo Script to shutdown any one server in the setup
echo This will showcase that in case a machine goes
echo down in our app, our overall system will still
echo be available. All the traffic would be handled
echo by the remaining servers in the setup.
echo We will be shutting down server1.
pause

echo Example for inputting directory of the servers
echo C:/Harshit_007/Apps/myWebProject/"Load Balancer" this format and be careful with the spaces and dont put the extra "/"
set /p tomcat_dir=Input the path for the root tomcat directory:
echo %tomcat_dir% The location of tomcat servers

echo Shutting down the server server1 ...
cd %tomcat_dir%/server1/bin/
call ./shutdown.bat
echo The server 'server1' is down
echo Now run the tests for verifying how the load balancer is working now
pause