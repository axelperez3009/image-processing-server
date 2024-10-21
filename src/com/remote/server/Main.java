package com.remote.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            // Crear registro en el puerto 4321
            Registry registry = LocateRegistry.createRegistry(4321);

            // Crear instancia de ImageProcessingServer
            ImageProcessingServer server = new ImageProcessingServer();

            // Vincular el objeto remoto al nombre en el registro
            registry.rebind("remote", server);

            System.out.println("Servidor RMI iniciado correctamente.");

            // Crear instancia de ImageProcessingView y pasar el objeto remoto
            ImageProcessingView view = new ImageProcessingView(server);
            view.setVisible(true);
        } catch (RemoteException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
