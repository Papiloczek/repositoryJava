import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
class clientThread extends Thread {
    private BufferedReader is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;
    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }
    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;
        try {
            /*
             * Create input and output streams for this client.
             */

            is=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            os.println("SUBMITNAME");
            String name = is.readLine().trim();
            os.println("NAMEACCEPTED Witaj " + name + " na czacie. W celu opuszczenia czatu wpisz exit");
           // os.println("NAMEACCEPTED Podaj swój login ponownie");
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println("MESSAGE *** Nowy użytkownik " + name + " podłączył się do czata ***");

                }
            }
            while (true) {
                String line = is.readLine();
                if (line.startsWith("exit")) {
                    break;
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null) {
                        threads[i].os.println("MESSAGE <" + name + ">" + line);
                    }
                }
            }
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println("MESSAGE *** Użytkownik " + name + " opuścił czat ***");
                }
            }
            os.println("MESSAGE   Do zobaczenia " + name + " następnym razem! ");
            /*
             * Clean up. Set the current thread variable to null so that a new client
             * could be accepted by the server.
             */
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }
            /*
             * Close the output stream, close the input stream, close the socket.
             */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}