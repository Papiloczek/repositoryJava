import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
/*
 * A chat server that delivers public and private messages.
 */
public class MultiThreadChatServer {
    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;
    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 5;
    private static final clientThread[] threads = new clientThread[maxClientsCount];
    public static void main(String args[]) {
// The default port number.
        int portNumber = 2222;
        if (args.length < 1) {
            System.out.println(
                    "Usage: java MultiThreadChatServer <portNumber>\n" + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }
        /*
         * Create a client socket for each connection and pass it to a new
         * client thread.
         */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("BUSY Serwer zajęty, spróbuj później.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}