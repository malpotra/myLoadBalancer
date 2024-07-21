@echo off
echo Script to start all the tomcats in the setup

set /p num_of_servers=Number of servers(put two if you have not done any changes to the provided code):
echo %num_of_servers% Server(s) are configured

echo Example C:/Harshit_007/Apps/myWebProject/"Load Balancer" this format and be careful with the spaces and dont put the extra "/"
set /p tomcat_dir=Input the path for the root tomcat directory:
echo %tomcat_dir% The location of tomcat servers

set incr_var=1
:loop
    if %incr_var% gtr %num_of_servers% goto exitloop

    set curr_tomcat=%tomcat_dir%/server%incr_var%/bin/
    cd %curr_tomcat%
    call ./startup.bat
    echo Server %incr_var% is up

    set /a incr_var=incr_var+1
    goto loop
:exitloop

echo Starting the proxy server...
cd %tomcat_dir%/reverse-proxy/bin/
call ./startup.bat
echo The proxy server is also up
echo Great now the setup is complete and lets now test the thing
pause