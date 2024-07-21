# A Loader Balancer Application
A software load balancer written using Java Servlets
## Required Tools/Softwares to successfully verify balancing
1. Java 8+ (ensure the JDK is properly installed and configured on system)
   ![On My System](https://github.com/malpotra/myLoadBalancer/assets/56645001/550ac4a8-d46f-41e8-bdab-131c403e0e80)
2. Apache Tomcat (version 10.0.8) (will be our server on which our servlets will be deployed)
3. Apache Jmeter (version 5.6.3) (to test our load balancer) whether its working correctly or not.
4. servlet-api.jar for compiling the java servlets found in the tomcat lib directory. Its not required to know its details but might be a case where we may have to explicitly provide in the class path to compile our servlets. I did not face any issue. Don't worry if there's any issue will provide the commands to compile the code.

## Overview of the setup
![image](https://github.com/malpotra/myLoadBalancer/assets/56645001/9c90c84e-ba74-44f4-b2b3-725cbb28356a)
### Proxy Servers and Load Balancer
Have implemented a **software** load balancer (there are **hardware** load balancers as well not going into that) which will be acting as a [reverse proxy](https://www.cloudflare.com/learning/cdn/glossary/reverse-proxy/) and will be exposed to the internet. Anyone on the web trying to request a resource from my website will be interacting with this server.

Let's say my website is called thenextbigthing.com and my proxy server's address is 10.2.2.5 (just some random IP).

When anyone enters https://thenextbigthing.com in their browser post the DNS resolution the request would be directed to this server. First the enycrption keys are generated as per the HTTPS protocol and once this has been done every resource required by the user from our website is fetched from the **application servers** to which the load balancer routes the request.

Once the server to which the request will be forwarded is decided the required action is performed by it and the response is returned to the proxy server which then responds to the requester.
Its a proxy as it never performed any business operation/logic and the user interacted only with the proxy it does not know about the actual **application server**.

Read more about proxy servers [here](https://www.cloudflare.com/learning/cdn/glossary/reverse-proxy/) or from any other online resource. Benefits of using a proxy server are listed below:
1. **Load Balancing** (what we will do) to prevent overloading a single server, allows **horizontal scaling** of the application server layer a new server can just be added to the available pool. As no business logic resides on the proxy server it just acts an orchestrator or mediator between the user and the application.
2. **Caching** static content (ie images/html/javascript/css that will be same throughout the application) at the Proxy server.
3. Enable **rate limiting** ensuring our actual application servers (which are also connected with our data persistence layer) are safe from DOS attacks.
4. **SSL encryption** only at the proxy reduces a lot of overhead.
5. Can be used as an **API gateway** as well.

**Note: The proxy server should be capable of handling multiple requests, encryption, rate limiting and caching so must be having optimal compute power.**

## Repo Structure
![image](https://github.com/user-attachments/assets/e444b0b6-029b-4f7c-b261-1f31d048cd2f)

The top level directories and what they contain are listed below
1. **jMeterTestPlansAndResults**: To test the functionality have added the required .jmx files along with the results screenshots.
2. **servlets**: All JSP servlets are in this directory. The three subdirectories are three servlets that will deployed on 3 different tomcats. As the name suggests the proxy servlet (our load balancer) in the sub-directory reverse-proxy, server1 and server2 are the application servlets that contain our application logic. Ideally this will be identical but just to show that both are being used have slightly modified the playload this will help us in testing and verifying our results as well.
3. **setupScripts**: Batch scripts that will be required for code compilation and simulating test scenarios
4. **tomcatConfiguration**: The configuration required at all the running Tomcats. Port details mostly.

**Note: In an actual setup all this code will be deployed on separate machines connected over the network, for learning purpose all this can be deployed on the same machine at different ports.**

## Starting and using the project
**Windows machine only**
1. Prepare all the tomcat servers (i have taken three feel free to add as many as you like provided you have also gone through the code)
   
   ![image](https://github.com/user-attachments/assets/21090d43-10c0-4abe-b754-37f10a4ed7b8)
   
   We will not be changing any other configuration or file in the server, all properties will be managed through a script provided in the project.
   **Ensure all the directory names are exactly same**
3. The **revere-proxy** is our load balancer server, **server1** and **server2** are the application servers.
4. Naivigate to the directory **/setupScripts** it contains all the batch scripts that will help us in managing our code easily. **All scripts must be excuted from this directory.**
5. First execute **replace_server_properties.bat** this will replace the server properties for all the tomcats that are required. It will ask for 2 user inputs
   1. The number of servers in the setup (2).
   2. The path where all the tomcat servers are.
      ![image](https://github.com/user-attachments/assets/4915f251-ef5c-42d7-b8cb-aa583185ed34)
6. Now let's first execute the script **compile.bat**. It will ask for 2 user inputs
   1. The number of servers in the setup (2).
   2. The path where all the tomcat servers are.
   ![image](https://github.com/user-attachments/assets/a3f0f185-4fc6-49ec-9395-bc3de92fde34)
   All our code has been compiled and moved to the corresponding server for deployment
7. We have to start the servers with the script, **startup.bat**.  It will ask for 2 user inputs
   1. The number of servers in the setup (2).
   2. The path where all the tomcat servers are.
   Lets verify whether three servers are actually running or not using the netstat command
   ```
   netstat -a -b
   ```
   ![image](https://github.com/user-attachments/assets/4c5ce437-dde9-402e-ad30-5fe79ee253b0)
   1. On ports 9000 and 9001 our application servers, 'server1' and 'server2' are listening.
   2. On port 9999 our proxy server is listening. Anyone who wants to access our application will be interacting with server listening on port 9999.
   3. These port configurations are in the **/tomcatConfiguration** directory and should not be changed because our code and test plans will also have to be changed for the same.
8. Verify by pasting this link in your browser (do this atleast 4-5 times, you will see the request has been handled by our application servers)
   ```
   http://localhost:9999/proxy/testing/test
   ```
   ![image](https://github.com/user-attachments/assets/766ab810-64f8-437e-89cd-57be2c3a1cc1)
   ![image](https://github.com/user-attachments/assets/1fa66974-6aea-4cbd-8a6f-0f8ed82c39b3)
   The code written for both the application servers server1 and server2 is exactly the same only thing different is the response they have are just giving out their server id 1 or 2.
9. Run the tests for jMeter.
   1. Load the test plans into your jMeter installation test plans are in the directory **jMeterTestPlansAndResults/testPlans**
      Directory structure
      ![image](https://github.com/user-attachments/assets/c9a2fde4-d3c3-43bd-99db-2456ec88a246)
      Choose this file
      ![image](https://github.com/user-attachments/assets/48c0661d-8f9e-4ebb-a11e-fecbae31176b)
   2. Do not change anything just simply click on run, have added step by step screenshots in the **jMeterTestPlansAndResults/results/withTwoServers/** folder
      The Thread group configuration (1000 threads with a ramp up period of 1 second)
      ![image](https://github.com/user-attachments/assets/7450bcd1-d77d-46fd-b5e7-d85dd86cef20)
      The HttpRequest Sampler details of the proxy server are here
      ![image](https://github.com/user-attachments/assets/2865265a-de6b-41b3-ae85-08ea9d0065c6)
      Extractor for getting the payload from the proxy returned, basically we want to see which server has responded
      ![image](https://github.com/user-attachments/assets/bae435cf-4fc6-49db-a7c2-8429c5aeef02)
      The assertion that we will be using to count requests distribution
      ![image](https://github.com/user-attachments/assets/068c712b-3464-4271-a468-e9a927618ccb)
      Requests handled by server 1
      ![image](https://github.com/user-attachments/assets/cb041b29-d2ff-4bf2-a530-d4df4a7bc250)
      Requests handled by server 2
      ![image](https://github.com/user-attachments/assets/fc0f7e1a-3d04-4fb2-a36f-5fd5f8e745b6)
      As can be seen both have handled 500 requests each that means our load balancer is working correctly it has distributed the load evenly to both the servers as per our scheduling algorithm (round robin scheduling)
   3. You can either manually stop the servers or can use the script **shutdown.bat**. It will be expecting 2 user inputs
      1. The number of servers in the setup (2).
      2. The path where all the tomcat servers are.
10. Scenario where one application server goes down. As discussed in the earlier section a major reason of having sych a setup is to ensure our system is highly available.
    1. Execute the script **one_server_goes_down.bat**.
    2. Run the tests in step 8, have added step by step screenshots in the **jMeterTestPlansAndResults/results/whenOneServerIsDown/** folder.
       1. Verify using the netstat command
       2. ![image](https://github.com/user-attachments/assets/70d55b6d-7099-401c-9db4-83caf9fec880) The server listening on port 9000 is not there anymore
       3. Load distribution
          As expected no requests were handled by server 1
          ![image](https://github.com/user-attachments/assets/2ceb3b5b-8421-412d-9efc-5c73ae544551)
          Server 2 was the only available server so it handled all the traffic
          ![image](https://github.com/user-attachments/assets/deaa208a-cff3-4740-a16a-ddaa8c4c8bbb)
11. Have added this section to show the importance of the synchronized block in our LoadBalancerImplementation class.
    1. Switch to the branch **synch-block-removed**
       ![image](https://github.com/user-attachments/assets/ddad243c-36b0-498e-96cf-34db7897745e)
    2. Repeat steps 5 to compile the code if your servers are already running then they will automatically detect any classes change and will redeploy all the servlets. If not then simply repeat step 6.
    3. Running the tests
       1. The code block we changed
          ``` java
          private synchronized String serverToBeUsed() {
              lastUsedID = (lastUsedID + 1) % serverPool;
              LOGGER.info(String.format("Server Number %d is used", lastUsedID));
              return servers.get(lastUsedID);
          }
          ``` 
          We haved **removed** the **synchronized** keyword and the **volatile** keyword from this block
          ``` java
              private static volatile int lastUsedID = -1;
          ```
          This will be leading to uneven distribution of the incoming requests as multiple threads might be trying to modify the static variable **lastUsedID** at the same time, the **synchronized** keyword ensured that the fuction serverToBeUsed() is executed by only thread at a time and rest all are waiting (essentially we had put a **mutex** lock on the variable **lastUsedID** in the previous implementation of the block).
       2. Run the tests 2-3 times and you will observe the distribution to the servers is random. Screenshots are provided in the folder **jMeterTestPlansAndResults/results/withoutConsideringTheConcurrencyClassicRaceCondition/**
          Requests that were handled by Server 1
          ![image](https://github.com/user-attachments/assets/274326bd-e2cd-45ee-8f5a-791033bb8291)
          Requests that were handled by Server 2
          ![image](https://github.com/user-attachments/assets/db23d432-2d80-4223-8129-0378ec326ca5)
          Running the tests again
          ![image](https://github.com/user-attachments/assets/f1ee601e-0070-46de-94d7-1d72d47930ff)
       3. You can either manually stop the servers or can use the script **shutdown.bat**. It will be expecting 2 user inputs
          1. The number of servers in the setup (2).
          2. The path where all the tomcat servers are.       
## Conclusion
Hope you enjoyed reading this article and were successfully able to reproduce the results. Do share your feedback. 

Maybe we might need to deploy this on separate machines and then can then implement a weighted round robin balancing technique that will distribute load according to their weights (that may be decided based on the compute available on the servers). 

I am trying to complete a series of projects that will involve a load balancer, a persistent datasource with querying capabilities and an in memory key value store.

The end goal is to run a complete website on these integrated systems.




   


   

   


