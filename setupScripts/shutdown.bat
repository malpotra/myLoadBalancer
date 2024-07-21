@echo off
echo Script to shutdown all the tomcats in the setup

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
    call ./shutdown.bat
    echo Server %incr_var% is down

    set /a incr_var=incr_var+1
    goto loop
:exitloop

echo Shutting down the proxy server...
cd %tomcat_dir%/reverse-proxy/bin/
call ./shutdown.bat
echo The proxy server is also down
echo Thank you for successfully setting up this project and using it.
echo Please do share your comments or suggestions. What can be done better or is there anything
echo that's not correct. Open for feedback.
echo : )
pause