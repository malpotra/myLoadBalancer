@echo off
echo Script to replace the server.xml properties
echo for your tomcat servers with ones provided in
echo the project.
pause

set /p num_of_servers=Number of servers(put two if you have not done any changes to the provided code):
echo %num_of_servers% Server(s) are configured

echo Example for inputting directory of the servers
echo C:/Harshit_007/Apps/myWebProject/"Load Balancer" this format and be careful with the spaces and dont put the extra "/"
set /p tomcat_dir=Input the path for the root tomcat directory:
echo %tomcat_dir% The location of tomcat servers

set incr_var=1
:loop
    if %incr_var% gtr %num_of_servers% goto exitloop

    set curr_tomcat=%tomcat_dir%/server%incr_var%/conf
    robocopy ../tomcatConfiguration/server%incr_var% %curr_tomcat% server.xml

    echo Server %incr_var% properties changed...

    set /a incr_var=incr_var+1
    goto loop
:exitloop

echo All servers properties changed now changing the proxy server property
robocopy ../tomcatConfiguration/reverse-proxy %tomcat_dir%/reverse-proxy/conf server.xml
echo All properties changed now proceed to server startup
pause