import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

 class VPNBasicServer {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888); // Server port

            System.out.println("VPN Server started. Listening on port 8888...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a thread to handle each client connection
                Thread clientThread = new Thread(() -> {
                    try {
                        // Forward client's data to the remote server
                        Socket remoteSocket = new Socket("remote_server_ip", 8888); // Remote server IP and port

                        // Client to remote server data transfer thread
                        Thread clientToRemoteThread = new Thread(() -> {
                            try {
                                InputStream clientIn = clientSocket.getInputStream();
                                OutputStream remoteOut = remoteSocket.getOutputStream();

                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = clientIn.read(buffer)) != -1) {
                                    remoteOut.write(buffer, 0, bytesRead);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        clientToRemoteThread.start();

                        // Forward remote server's response to the client
                        InputStream remoteIn = remoteSocket.getInputStream();
                        OutputStream clientOut = clientSocket.getOutputStream();

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = remoteIn.read(buffer)) != -1) {
                            clientOut.write(buffer, 0, bytesRead);
                        }

                        // Close the client and remote sockets
                        clientSocket.close();
                        remoteSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
