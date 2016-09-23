
**How to reproduce the problem**

- Checkout and build the code using maven

- Copy the JAR on the box/VM where you want to run it.

- execute the code using following command (here 50000 is debug port)
        
        java -jar -Dhosts="1.1.1.1,2.2.2.2"  -Xrunjdwp:server=y,transport=dt_socket,address=50000,suspend=n ignite-thread-block-issue-1.0-SNAPSHOT-fat.jar
        
    Here "1.1.1.1,2.2.2.2" are comma separated IP of hosts/nodes in cluster. Make sure you run this app on 2 nodes, and this list of hosts has both the IPs.
    
- You should see the logs that we are able to read the cache fine.
   
- From command you can see that we are opening the remote port(50000).

- Now remotely connect to one of the VM on remote port 50000 and put a breakpoint in ProblemProducer class at line 46. Make sure 50000 is open on firewall.
 
- In ProblemProducer class we read/write on Ignite cache every 3 seconds. When you put a breakpoint for more than 5 seconds then other node will consider the debugged node as segmented.

- After stopping the execution for more than 5 seconds, you can resume the execution and remove the break point. Going forward you will see the message that reading from Ignite cache is getting blocked.

- Alternatively you can run the application in IDE from AppStarter.java
    