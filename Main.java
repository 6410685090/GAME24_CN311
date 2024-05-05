import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        int GAME_SERVER_PORT = 5000;
        String serverAddress = "localhost";

        Client c = new Client(GAME_SERVER_PORT , serverAddress);
        c.start();

    }

}
