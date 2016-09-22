
**How to reproduce the problem**

- Checkout and build the code using maven
- execute the code using following command
        
        java -jar -Dhosts="1.1.1.1,2.2.2.2" ignite-thread-block-issue-1.0-SNAPSHOT-fat.jar
        
    Here "1.1.1.1,2.2.2.2" are comma separated IP of hosts/nodes in cluster.
   
- Now you must see logs on console telling when the thread is blocked.

- Logic to reproduce the problem is written in ProblemProducer.java + the documentation.

- Alternatively you can run the application in IDE from AppStarter.java
    