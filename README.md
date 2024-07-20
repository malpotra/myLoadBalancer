# A Loader Balancer Application
A software load balancer written using Java Servlets
## Required Tools/Softwares to successfully verify balancing
1. Java 8+ (ensure the JDK is properly installed and configured on system)
   ![On My System](https://github.com/malpotra/myLoadBalancer/assets/56645001/550ac4a8-d46f-41e8-bdab-131c403e0e80)
2. Apache Tomcat (version 10.0.8) (will be our server on which our servlets will be deployed)
3. Apache Jmeter (version 5.6.3) (to test our load balancer) whether its working correctly or not.

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
![image](https://github.com/malpotra/myLoadBalancer/assets/56645001/d55dbd5d-68cd-4c2f-acd2-43996124b163)

The top level directories and what they contain are listed below
1. **jMeterTestPlansAndResults**: To test the functionality have added the required .jmx files along with the results screenshots.
2. **servlets**: All JSP servlets are in this directory. The three subdirectories are three servlets that will deployed on 3 different tomcats. As the name suggests the proxy servlet (our load balancer) in the sub-directory reverse-proxy, server1 and server2 are the application servlets that contain our application logic. Ideally this will be identical but just to show that both are being used have slightly modified the playload this will help us in testing and verifying our results as well.
3. **tomcatConfiguration**: The configuration required at all the running Tomcats. Port details mostly.
