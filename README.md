# Distributed_System
Implementated distributed system in Java

DEMS implemented in three ways RMI, CORBA and WEBSERVICES.

The DEMS has functionality for client and server. The system has client acting in two different roles. If he is a client he has limited functions like booking and cancelling event and getting a schedule of events booked. If he is an event manager he can add or remove an event and also get the total availability for an event at any point of time. The system distinguishes between a customer and a manager using the unique client id. The client is an alpha numeric combination of letters and numbers where in the first three letters denote the city in which the client resides and the fourth character denotes the difference. If it’s C it implies it to be a customer and if it’s M it denotes him/her to be a manager. The remaining is set to number combinations. Also the events can take place in three different times of a day. If an event is scheduled for morning then it’s event id has the letter M appended to it. If it’s afternoon it has A and if it’s evening then it has E.

• bookEvent (eventId, customerId, eventType)
The function enables the customer to book for the event. Once booked the booking capacity will be decremented by 1 and the change will be reflected when the user calls the getBookingSchedule() function.

• cancelEvent(eventid, customerId)
This event removes an entry for the user. The total count for availability will now be reset at the server side.

• getBookingSchedule(customerId)
This function allows the user to get a schedule of all events lined up for him/her to attend. This is also a cross server function since the user is also eligible to attend events outside his city.
The Event manager has the above mentioned functionalities and also some additional functions like the ones below. The above mentioned functions are available to the manager because a manager can also act as an user and can attend the events.

• addEvent(eventId, eventType, bookingCapacity)
This function allows the manager to post an event to the system. After posting the customers can book the events. It is to be noted that only managers pertaining to that zone can add/remove an event for that region. Cross server functions are not applicable in this scenario.

• removeEvent( eventId, eventType)
This function allows the manager to remove an event that is scheduled.

• listEventAvailabilty(eventType)
This functionality allows the manager to view the total seats booked for an event.
These functionalities are implemented with java RMI. RMI is a technology that allows one object of JVM to access a function that is being operated in another function in a totally different object. The simple architecture of RMI is as follows.

• swapEvent()
This method is used to switch an event for another. If they violate the booking semantics then the swap will not proceed as expected. Also swapping a non existent event is not possible.
The Event manager has the above mentioned functionalities and also some additional functions like the ones below. The above mentioned functions are available to the manager because a manager can also act as an user and can attend the events.


FINAL PROJECT:

The main objective of this project is to make use of our CORBA Distributed Event Management System implementation and make it to be software failure tolerant and/or highly available under process crash failure using the replica of our project. The Distributed Event Management System consists of three servers (Montreal, Ottawa, Toronto) and one client.The distributed system will be used by manager
and customer to perform various roles and functionalities in three different cities. In the distributed system based on customer/Manger logged in, the authorised request send to the respective server via Front End(FE) through CORBA call. Then the FE will make the UDP call to the sequencer. Also FE responsible for checks the majority of responses from the RM which helps to maintain the DEMS highly
available under process crash failure and software failure. Then the sequencer will make Multicast UDP to all the Replication Manager(RM). The RM processing the request and send back the response to the FE. Also, RM responsible for data synchronisation, server bug and crash implementation. Finally, FE send back the response to the client. The system is implemented in such a way that client wouldn’t know the location of the server where they interacted and produce accurate response even in software failure and process crash.Overall it maintains the system highly available in both scenarios. To experiment software failure, manually creating bug in the server of replica 1 that will detected by FE and the process crash is implemented in replica 2.
