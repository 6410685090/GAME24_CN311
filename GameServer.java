import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer extends Thread {
    private ServerSocket serverSocket;
    private static final int GAME_SERVER_PORT = 5000;


    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
        gameServer.start();
    }

    public GameServer() {
        try {
            serverSocket = new ServerSocket(GAME_SERVER_PORT);
            System.out.println("Server created on port " + GAME_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Server started. Listening for connections...");
        try {
            while (true) {
                
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection accepted from " + clientSocket.getInetAddress());
                
                GameServerHandler client = new GameServerHandler(clientSocket);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

}
