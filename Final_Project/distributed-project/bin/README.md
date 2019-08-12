#### Reference: https://github.com/sun-haiyang/DRMS_COMP6231

### Team Tasks
Assuming that server failure could be due to either a single software bug (nonmalicious Byzantine failure) or a single process crash (selected at server initialization), design your actively replicated server system.

(Each Student) Modify the server implementation from Assignment 2 so that it can work as a (non-CORBA) server replica. A server replica receives the client requests with sequence numbers and FE information from the sequencer, executes the client requests in total order according to the sequence number and sends the result back to the FE.

#### Student 1 
- Design and implement the front end (FE) which receives a client request as
a CORBA invocation, forwards the request to the sequencer, receives the results from
the replicas and sends a single correct result back to the client as soon as possible. The
FE also informs all the RMs of a possibly failed replica that produced incorrect result.

#### Student 2
- Design and implement the replica manager (RM) which creates and
initializes the actively replicated server system. The RM also implements the failure
detection and recovery for the required type of failure.

#### Student 3
- Design and implement a failure-free sequencer which receives a client
request from a FE, assigns a unique sequence number to the request and reliably
multicast the request with the sequence number and FE information to all the three
server replicas.

### Integration (Team) 
- Integrate all the modules properly, deploy your application on a local area network, and test the correct operation of your application using properly designed test runs. You may simulate a software failure by returning a random result and a process crash by killing that process while the application is running.
