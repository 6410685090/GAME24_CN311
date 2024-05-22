import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;


public class GameServerHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static int[] score = new int[4];
    private static String[] player = {"No Player" , "No Player" , "No Player" , "No Player"};
    private static int index = 0;
    public static int[] problem = new int[4];
    public static int time;
    private static boolean createThreadCheck = false;

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
        if(!GameServerHandler.createThreadCheck){
            GameServerHandler.createThreadCheck = true;
            Thread p = new Thread(){
                public void run() {
                    while (true) {
                        GameServerHandler.problem = getProblem();
                        System.out.println("New problem generated: " + Arrays.toString(GameServerHandler.problem));
                        GameServerHandler.time = 30;
                        while (GameServerHandler.time != 0) {
                            GameServerHandler.time--;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            p.start();
        }
        
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                if(inputLine.equals("exit")){
                    break;
                }

                if(inputLine.equals("isFull")){
                    if(index >= 4){
                        out.println("true");
                    } else {
                        out.println("false");
                    }
                }

                if(inputLine.startsWith("add")){
                    String[] str = inputLine.split(",");
                    String name = str[1];
                    if(index < 4){
                        player[index]=name;
                        index++;
                    } else {
                        System.out.println("Max player reached");
                        index = 0; // test
                        player[index]=name; // test
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
                    res = player[0] + ": " + score[0] + " points";
                    result[0] = res;
                    res = player[1] + ": " + score[1] + " points";
                    result[1] = res;
                    res = player[2] + ": " + score[2] + " points";
                    result[2] = res;
                    res = player[3] + ": " + score[3] + " points";
                    result[3] = res;
                    out.println(Arrays.toString(result));
                }
                if(inputLine.equals("getProblem")){
                    out.println(Arrays.toString(problem));
                }
                if(inputLine.equals("time")){
                    out.println(time);
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

    public int getRandom(){
        Random rand = new Random();
        int n = rand.nextInt(9)+1;
        return n;
    }

    public int[] getProblem(){
        int[] newproblem = new int[4];
        for(int i = 0; i < 4; i++){
            newproblem[i] = getRandom();
        }
        return newproblem;
    }



}
