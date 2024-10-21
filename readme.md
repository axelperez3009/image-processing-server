# Distributed Image Processing System

## Overview

This project implements a distributed image processing system using Java RMI (Remote Method Invocation). It allows for parallel processing of images by dividing the work among multiple connected clients.

## Features

- Server-client architecture using Java RMI
- Distributed image processing
- Real-time client management (add, remove, block, reactivate)
- Server GUI for monitoring connected clients
- Image serialization for network transmission

## Components

1. **Server**
   - `ImageProcessingServer`: Main server logic
   - `ImageProcessingView`: Server GUI
   - `Main`: Server entry point

2. **Interfaces**
   - `InterfaceServer`: Defines remote methods for clients
   - `InterfaceClient`: Defines methods for server-to-client communication

3. **Utilities**
   - `SerializableImage`: For image serialization

## Setup

1. Ensure you have Java Development Kit (JDK) installed.
2. Compile all Java files in the `src` directory.
3. Start the RMI registry:
   ```
   rmiregistry 4321
   ```
4. Run the server:
   ```
   java com.remote.server.Main
   ```

## Usage

1. Start the server using the instructions above.
2. Connect clients to the server (client implementation not provided in this repository).
3. Use the server GUI to monitor connected clients.
4. Send images from clients for processing.
5. The server will distribute image parts to connected clients, recombine processed parts, and send the result back to all clients.

