package com.remote.server;

import com.remote.client.InterfaceClient;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class ImageProcessingServer extends UnicastRemoteObject implements InterfaceServer {
    private final ArrayList<InterfaceClient> clients;
    private final ArrayList<InterfaceClient> blockedClients;
    private Map<Integer, BufferedImage> receivedParts;
    private int totalParts;

    public ImageProcessingServer() throws RemoteException{
        super();
        this.clients = new ArrayList<>();
        this.blockedClients = new ArrayList<>();
        this.receivedParts = new HashMap<>();
    }
    
    @Override
    public synchronized void addClient(InterfaceClient client) throws RemoteException {
        this.clients.add(client);
    }
    
    @Override
    public synchronized Vector<String> getListClients() throws RemoteException {
        Vector<String> list = new Vector<>();
        for (InterfaceClient client : clients) {
            list.add(client.getName());
        }
        return list;
    }

    @Override
    public boolean checkUsername(String username) throws RemoteException {
        boolean exist = false;
        for(int i=0;i<clients.size();i++){
            if(clients.get(i).getName().equals(username)){
                exist = true;
            }
        }
        for(int i=0;i<blockedClients.size();i++){
            if(blockedClients.get(i).getName().equals(username)){
                exist = true;
            }
        }
        return exist;
    }
    public static BufferedImage convertImageIconToBufferedImage(ImageIcon icon) {
        // Obtener el ancho y alto del ImageIcon
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        // Crear un BufferedImage con el mismo ancho y alto del ImageIcon
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Obtener el Graphics2D del BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();

        // Dibujar el ImageIcon en el BufferedImage
        g2d.drawImage(icon.getImage(), 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }
    public BufferedImage combineImageParts() {
        int totalWidth = 0;
        int maxHeight = 0;
        for (BufferedImage part : receivedParts.values()) {
            totalWidth += part.getWidth();
            maxHeight = Math.max(maxHeight, part.getHeight());
        }

        BufferedImage joinedImage = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = joinedImage.createGraphics();
        int currentX = 0;
        for (BufferedImage part : receivedParts.values()) {
            g2d.drawImage(part, currentX, 0, null);
            currentX += part.getWidth();
        }

        g2d.dispose();

        return joinedImage;
    }
    
    @Override
    public synchronized void receiveImagePart(ImageIcon icon, String clientName, int partIndex) throws RemoteException {
        System.out.println("Recibiste la parte número: " + partIndex + " Del cliente: " + clientName);
        System.out.println("Partes Recibidas: " + receivedParts.size());
        System.out.println("Partes Totales: " + totalParts);
        InterfaceClient client = getClientByName(clientName);
        client.setpartImage(icon);
        BufferedImage image = convertImageIconToBufferedImage(icon);
        receivedParts.put(partIndex, image);
        if (receivedParts.size() == totalParts) {
            BufferedImage joinedImage = combineImageParts();
            ImageIcon filteredIcon = new ImageIcon(joinedImage);
            for (InterfaceClient cliente : clients) {
                cliente.setfilteredImage(filteredIcon);
            }
            receivedParts.clear();
        }
        
    }
   
    @Override
    public void divideAndSendImage(ImageIcon imageIcon) throws RemoteException {
        BufferedImage image = convertImageIconToBufferedImage(imageIcon);
        try {
            int numClients = clients.size();
            int partWidth = image.getWidth() / numClients;
            int partIndex = 0;
            this.totalParts = numClients;
            for (int i = 0; i < numClients; i++) {
                int startX = i * partWidth;
                int endX = (i + 1) * partWidth;
                if (i == numClients - 1) {
                    endX = image.getWidth();
                }
                BufferedImage imagePart = image.getSubimage(startX, 0, endX - startX, image.getHeight());
                ImageIcon icon = new ImageIcon(imagePart);
                InterfaceClient client = clients.get(i);
                client.receiveImagePart(icon, partIndex);
                System.out.println("Imagen enviada al cliente: " + client.getName());
                partIndex++;
            }
        } catch (IOException ex) {
            Logger.getLogger(ImageProcessingServer.class.getName()).log(Level.SEVERE, null, ex);
        }             
    }

    @Override
    public void blockClient(List<String> clients) throws RemoteException {
        System.out.println("Blocking clients: " + clients);
        for (String client : clients) {
            InterfaceClient  blockedClient = getClientByName(client);
            if (blockedClient != null) {
                this.blockedClients.add(blockedClient);
                this.clients.remove(blockedClient);
                System.out.println("Client blocked: " + client);
            } else {
                System.out.println("Client not found: " + client);
            }
        }
    }

    @Override
    public void removeClient(List<String> clients) throws RemoteException {
        System.out.println("Removing clients: " + clients);
        for (String client : clients) {
            InterfaceClient  removedClient = getClientByName(client);
            if (removedClient != null) {
                this.clients.remove(removedClient);
                System.out.println("Client removed: " + client);
            } else {
                System.out.println("Client not found: " + client);
            }
        }
    }

    @Override
    public void removeClient(String client) throws RemoteException {
        System.out.println("Removing client: " + client);
        InterfaceClient  removedClient = getClientByName(client);
        if (removedClient != null) {
            this.clients.remove(removedClient);
            System.out.println("Client removed: " + client);
        } else {
            System.out.println("Client not found: " + client);
        }
    }

    @Override
    public void reactiveClient(List<String> clients) throws RemoteException {
        System.out.println("Reactivating clients: " + clients);
        for (String client : clients) {
            InterfaceClient  reactivatedClient = getBlockedClientByName(client);
            if (reactivatedClient != null) {
                this.clients.add(reactivatedClient);
                this.blockedClients.remove(reactivatedClient);
                System.out.println("Client reactivated: " + client);
            } else {
                System.out.println("Blocked client not found: " + client);
            }
        }
    }

    // Método auxiliar para obtener un cliente por nombre
    private InterfaceClient getClientByName(String name) throws RemoteException {
        for (InterfaceClient  client : this.clients) {
            if (client.getName().equals(name)) {
                return client;
            }
        }
        return null;
    }

    // Método auxiliar para obtener un cliente bloqueado por nombre
    private InterfaceClient  getBlockedClientByName(String name) throws RemoteException {
        for (InterfaceClient  client : this.blockedClients) {
            if (client.getName().equals(name)) {
                return client;
            }
        }
        return null;
    }
}
