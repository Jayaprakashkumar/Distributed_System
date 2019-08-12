package com.concordia.clientInterface;


import java.rmi.*;

public interface  ClientServerInterface extends Remote {

    public String AddEvent(String eventId, String eventType, Integer bookingCapactiy) throws java.rmi.RemoteException;

    public String RemoveEvent(String eventId, String eventType) throws java.rmi.RemoteException;
  
    public String ListEventAvailability(String eventType) throws java.rmi.RemoteException;

  
    
    public String BookEvent(String customerId, String eventId, String eventType) throws java.rmi.RemoteException;

    public String GetBookingSchedule(String customerId) throws java.rmi.RemoteException;

    public void CancelEvent(String cutomerId, String eventId, String eventType) throws java.rmi.RemoteException;

    //public String ServerCommunication(String customerId,String eventType,String eventId,String requestType) throws java.rmi.RemoteException;
    
    public void ResultHashMap() throws java.rmi.RemoteException;
    
}
