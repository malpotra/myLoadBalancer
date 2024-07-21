@echo off
echo Script to compile all source code and move to the correct directory

set /p num_of_servers=Number of servers(put two if you have not done any changes to the provided code):
echo %num_of_servers% Servers are configured

echo Now enter the path of the directory where tomcat servers are there
echo Example C:/Harshit_007/Apps/myWebProject/"Load Balancer" this format and be careful with the spaces and dont put the extra "/"
set /p tomcat_dir=Enter The path to the tomcat directory:
echo You have provided the path...
echo %tomcat_dir%

cd /d %CD%/../servlets/
echo Inside the servlets directory
echo %CD%
set i=1
:loop
    if %i% gtr %num_of_servers% goto exitloop
    set curr_serv=server%i%
    set curr_tomcat=server%i%

    echo Compiling for server %i%....
    echo javac -d ./%curr_serv%/WEB-INF/classes/ ./%curr_serv%/WEB-INF/src/*.java
    javac -d ./%curr_serv%/WEB-INF/classes/ ./%curr_serv%/WEB-INF/src/*.java
    echo Compiled. Now moving the complied classes to the target tomcat
    echo robocopy ./%curr_serv%/ %tomcat_dir%/%curr_tomcat%/webapps/testing/ /E Moving to %tomcat_dir%/%curr_tomcat%/webapps/testing/
    robocopy ./%curr_serv%/ %tomcat_dir%/%curr_tomcat%/webapps/testing/ /E
    set /a i=i+1
   goto loop
:exitloop

echo server codes compiled and moved to the appropriate server directories
echo Now compiling the exposed servlet code
set load_balancer=reverse-proxy
echo Compiling the code in %CD%/%load_balancer%/
echo javac -d ./%load_balancer%/WEB-INF/classes/ ./%load_balancer%/WEB-INF/src/loadBalancer/*.java
javac -d ./%load_balancer%/WEB-INF/classes/ ./%load_balancer%/WEB-INF/src/loadBalancer/*.java
echo javac -cp ./%load_balancer%/WEB-INF/classes/ -d ./%load_balancer%/WEB-INF/classes/ ./%load_balancer%/WEB-INF/src/exposedPkg/*.java
javac -cp ./%load_balancer%/WEB-INF/classes/ -d ./%load_balancer%/WEB-INF/classes/ ./%load_balancer%/WEB-INF/src/exposedPkg/*.java
echo Compiled the code, now moving to the tomcat directory
echo robocopy ./%load_balancer%/ %tomcat_dir%/reverse-proxy/webapps/proxy/ /E
robocopy ./%load_balancer%/ %tomcat_dir%/reverse-proxy/webapps/proxy/ /E
echo Code compiled now proceed to servers startup script
pause