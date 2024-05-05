import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;
import java.util.Random;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class Client extends Thread {
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String name;
    private boolean connect;


    public Client(int port, String serverAddress) {
        connect = false;
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            {
                System.out.println("Connected to the server.");
                connect = true;
            }
        } catch (IOException e) {
            System.out.println("Error: Unable to connect to the server.");
            connect = false;
        }
    }

    public void run() {
        if (connect) {
            JFrame frame = new JFrame("Game Room");

            int base = 175;
            frame.setSize(base * 4, base * 3);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
    
            JLabel label = new JLabel("Enter Your Name");
            Font labelFont = label.getFont();
            label.setFont(new Font(labelFont.getName(), Font.PLAIN, 20));
    
            JTextField textField = new JTextField(35);
            textField.setSize(new Dimension(100,50));
            Font textFieldFont = textField.getFont();
            textField.setFont(new Font(textFieldFont.getName(), Font.PLAIN, 20));
    
            JButton EnterGame = new JButton("Enter Game");
    
            frame.add(label);
            frame.add(textField);
            frame.add(EnterGame);

            
            EnterGame.addActionListener(e -> {
                System.out.println("Button pressed");
                
                frame.getContentPane().removeAll(); 
                frame.setLayout(new GridLayout(3, 3));
               

                JLabel gameName = new JLabel("Game 24");
                Font gameNameFont = gameName.getFont();
                gameName.setFont(new Font(gameNameFont.getName(), Font.PLAIN, 20));

                String playerName = textField.getText();
                if (playerName.equals("")) {
                    playerName = "Player#" + new Random().nextInt(1000);
                }
                this.name = playerName;

                JLabel playerNameLabel = new JLabel("Player: " + playerName);
                Font playerNameFont = playerNameLabel.getFont();
                playerNameLabel.setFont(new Font(playerNameFont.getName(), Font.PLAIN, 20));
                
                JLabel problem = new JLabel("Problem: " + getRandom() + " " + getRandom() + " " + getRandom() + " " + getRandom());
                Font problemFont = problem.getFont();
                problem.setFont(new Font(problemFont.getName(), Font.PLAIN, 20));

                JPanel sPanel = new JPanel();
                sPanel.setLayout(new GridLayout(4, 1));

                JLabel score1 = new JLabel("");
                Font scoreFont = score1.getFont();
                score1.setFont(new Font(scoreFont.getName(), Font.PLAIN, 20));

                JLabel score2 = new JLabel("");
                score2.setFont(new Font(scoreFont.getName(), Font.PLAIN, 20));
                
                JLabel score3 = new JLabel("");
                score3.setFont(new Font(scoreFont.getName(), Font.PLAIN, 20));

                JLabel score4 = new JLabel("");
                score4.setFont(new Font(scoreFont.getName(), Font.PLAIN, 20));

                sPanel.add(score1);
                sPanel.add(score2);
                sPanel.add(score3);
                sPanel.add(score4);

                JLabel p2 = new JLabel("");
                
                JLabel timeLeftLabel = new JLabel("Time: 30");
                Font timeLeftFont = timeLeftLabel.getFont();
                timeLeftLabel.setFont(new Font(timeLeftFont.getName(), Font.PLAIN, 20));
                
                JLabel p4 = new JLabel("");
                
                JTextField p5 = new JTextField(35);
                p5.setSize(new Dimension(50,40));
                
                JButton submit = new JButton("Submit");
                submit.setPreferredSize(new Dimension(100, 50));


                final int[] timel = {30};

                Thread t2 = new Thread() {
                    public void run() {
                        timel[0] = 30;
                        while (true) {
                            timeLeftLabel.setText("Time: " + timel[0]);
                            try {
                                Thread.sleep(1000);
                                timel[0]--;
                                if (timel[0] == 0) {
                                    problem.setText("Problem: " + getRandom() + " " + getRandom() + " " + getRandom() + " " + getRandom());
                                    timel[0] = 30;
                                }
                            } catch (InterruptedException e) {
                                System.out.println("Error in t2");
                            }
                        }
                        
                    }
                };

                Thread t3 = new Thread(){
                    public void run(){
                        while (true){
                            try {
                                sendData("score");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                String[] data = recvStringArrayData();
                                score1.setText(data[0]);
                                score2.setText(data[1]);
                                score3.setText(data[2]);
                                score4.setText(data[3]);
                                Thread.sleep(1000);
                                
                            } catch (IOException | InterruptedException e) {
                                System.out.println("Error in t3");
                            }
                        }
                       
                    }
                };
                

                frame.add(gameName);
                frame.add(playerNameLabel);
                frame.add(sPanel);
                frame.add(p2);
                frame.add(problem);
                frame.add(timeLeftLabel);
                frame.add(p4);
                frame.add(p5);
                frame.add(submit);
                t2.start();
                t3.start();


                try{
                    sendData("add,"+name);
                } catch (IOException e1){
                    System.out.println("Error in add (sendData)");
                }
                
                submit.addActionListener(e1 -> {   
                    String answer = p5.getText();
                    boolean c = checkAns(answer);
                    if(c){
                        timel[0] = 30;
                        problem.setText("Problem: " + getRandom() + " " + getRandom() + " " + getRandom() + " " + getRandom());
                        try {
                            sendData("correct,"+name);
                        } catch (IOException e2) {
                            System.out.println("Error IN submit btn");
                        }
                    }
                    
                });

                frame.revalidate();
                frame.repaint();
            });
    
            frame.setVisible(true);
            
        }
        
    }

    public boolean checkAns(String answer){
        int sum = 0;
        answer = answer.trim();
        answer = clearWSpace(answer);
        int[] num = getNumber(answer);
        for(int i = 1; i < answer.length(); i = i+2){
            char c = answer.charAt(i);
            if (c < '0' || c > '9') {
                switch (c) {
                    case '+':
                        if(i == 1){
                            sum = num[0] + num[1];
                        }
                        if(i == 3){
                            sum = sum + num[2];
                        }
                        if (i == 5) {
                            sum = sum + num[3];
                        }
                        break;
                    case '-':
                        if(i == 1){
                            sum = num[0] - num[1];
                        }
                        if(i == 3){
                            sum = sum - num[2];
                        }
                        if (i == 5) {
                            sum = sum - num[3];
                        }
                        break;
                    case '*':
                        if(i == 1){
                            sum = num[0] * num[1];
                        }
                        if(i == 3){
                            sum = sum * num[2];
                        }
                        if (i == 5) {
                            sum = sum * num[3];
                        }
                        break;
                    case '/':
                        if(i == 1){
                            sum = num[0] / num[1];
                        }
                        if(i == 3){
                            sum = sum / num[2];
                        }
                        if (i == 5) {
                            sum = sum / num[3];
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        
        return sum == 24;
    }

    private String clearWSpace(String str){
        return str.replaceAll(" ", "");
    }

    private int[] getNumber(String answer){
        int[] res = new int[4];
        int index = 0;
        for(int i = 0; i < answer.length(); i = i + 2){
            char c = answer.charAt(i);
            int num = c - '0';
            res[index] = num;
            index++;
        }
        return res;
    }

    public int getRandom(){
        Random rand = new Random();
        int n = rand.nextInt(9)+1;
        return n;
    }

    public void sendData(String data) throws IOException {
        if (out != null) {
            out.println(data);
        } else {
            System.out.println("Cannot Send Data");
        }
    }

    public int[] recvINTArrayData() throws IOException {
        String receivedData = in.readLine();
        
        int[] res = null;           
        String[] strArray = receivedData.substring(1, receivedData.length() - 1).split(", "); // Remove brackets and split by ", "
        res = new int[strArray.length];
        try {
            for (int i = 0; i < strArray.length; i++) {
                res[i] = Integer.parseInt(strArray[i]);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error IN int array convert");
        }
        return res;
    }

    public String[] recvStringArrayData() throws IOException {
        String receivedData = in.readLine();
        String[] res = null;
        res = receivedData.substring(1, receivedData.length() - 1).split(", ");
        return res;
    }

}

