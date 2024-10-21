package com.remote.server;

import com.remote.client.InterfaceClient;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;

public interface InterfaceServer extends Remote {    
    
    Vector<String> getListClients() throws RemoteException;
    
    void addClient(InterfaceClient client) throws RemoteException;
    
    void blockClient(List<String> clients) throws RemoteException;
    
    void removeClient(List<String> clients) throws RemoteException;
    
    void removeClient(String clients) throws RemoteException;
    
    void reactiveClient(List<String> clients) throws RemoteException;
    
    boolean checkUsername(String username) throws RemoteException;
        
    void receiveImagePart(ImageIcon icon, String clientName, int partIndex) throws RemoteException;
    
    void divideAndSendImage(ImageIcon imageIcon) throws RemoteException;
    
}