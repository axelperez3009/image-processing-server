package com.remote.client;

import java.awt.image.BufferedImage;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.swing.ImageIcon;

public interface InterfaceClient extends Remote {
    
    BufferedImage loadImage(String imagePath) throws RemoteException;
    
    BufferedImage applyFilter(BufferedImage image, String filterName) throws RemoteException;
    
    BufferedImage splitImage(BufferedImage image, boolean isFirstHalf) throws RemoteException;
    
    String getName() throws RemoteException;
    
    void receiveImagePart(ImageIcon icon, int partIndex) throws RemoteException;
    
    void setfilteredImage(ImageIcon icon) throws RemoteException;
    
    ImageIcon getfilteredImage() throws RemoteException;
    
    void setpartImage(ImageIcon icon) throws RemoteException;
    
    ImageIcon getpartImage() throws RemoteException;
    
}
