import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;


public class GameServerHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static int[] score = new int[4];
    private static String[] player = {"No Player" , "No Player" , "No Player" , "No Player"};
    private static int index = 0;

    public GameServerHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("(Test In server)Client says: " + inputLine);

                if(inputLine.startsWith("add")){
                    String[] str = inputLine.split(",");
                    String name = str[1];
                    if(index < 4){
                        player[index]=name;
                        index++;
                    } else {
                        System.out.println("Max player reached");
                    }

                    
                }
                if(inputLine.startsWith("correct")){
                    String[] str = inputLine.split(",");
                    String name = str[1];
                    int i = indexOf(player, name);
                    score[i]++;
                }
                if(inputLine.equals("score")){
                    String[] result = new String[4];
                    String res = "";
                    res = player[0] + ": " + score[0];
                    result[0] = res;
                    res = player[1] + ": " + score[1];
                    result[1] = res;
                    res = player[2] + ": " + score[2];
                    result[2] = res;
                    res = player[3] + ": " + score[3];
                    result[3] = res;
                    out.println(Arrays.toString(result));
                }
                                
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int indexOf(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i; // Return the index if found
            }
        }
        return -1; // Return -1 if not found
    }



}
