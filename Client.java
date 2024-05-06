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
    public int[] number;
    public int[] checkNumber = new int[10];
    public int time = 30;
    public boolean alreadyAnswered = false;


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
                
                frame.getContentPane().removeAll(); 
                frame.setLayout(new GridLayout(3, 3));
               

                JLabel gameName = new JLabel("     Game 24");
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

                JLabel problem = new JLabel("Problem: ");
                Font problemFont = problem.getFont();
                    problem.setFont(new Font(problemFont.getName(), Font.PLAIN, 20));

                try{
                    sendData("getProblem");
                    number = recvINTArrayData();
                    problem.setText("Problem: " + number[0]  + " " + number[1] + " " + number[2] + " " + number[3]);
                }
                catch(IOException e2){
                    System.out.println("Error in getProblem");
                }

             

                JPanel sPanel = new JPanel();
                sPanel.setLayout(new GridLayout(4, 1));

                JLabel score1 = new JLabel("");
                Font scoreFont = score1.getFont();
                score1.setFont(new Font(scoreFont.getName(), Font.PLAIN, 17));

                JLabel score2 = new JLabel("");
                score2.setFont(new Font(scoreFont.getName(), Font.PLAIN, 17));
                
                JLabel score3 = new JLabel("");
                score3.setFont(new Font(scoreFont.getName(), Font.PLAIN, 17));

                JLabel score4 = new JLabel("");
                score4.setFont(new Font(scoreFont.getName(), Font.PLAIN, 17));

                sPanel.add(score1);
                sPanel.add(score2);
                sPanel.add(score3);
                sPanel.add(score4);

                JLabel info = new JLabel("     Can Use +, -, *, / to get 24 only");
                
                JLabel timeLeftLabel = new JLabel("Time: 30");
                Font timeLeftFont = timeLeftLabel.getFont();
                timeLeftLabel.setFont(new Font(timeLeftFont.getName(), Font.PLAIN, 20));
                
                JLabel warning = new JLabel("");
                Font warningFont = warning.getFont();
                warning.setFont(new Font(warningFont.getName(), Font.PLAIN, 20));
                                
                JTextField input = new JTextField(35);
                input.setSize(new Dimension(50,40));
                
                JButton submit = new JButton("Submit");
                submit.setPreferredSize(new Dimension(100, 50));

                Thread timer = new Thread() {
                    public void run() {
                       
                        while (true) {
                            try{
                                sendData("time");
                                time = recvINTData();
                                timeLeftLabel.setText("Time: " + time);
                            } catch (IOException e1){
                                System.out.println("Error in time");
                            }
                            try{
                                sendData("getProblem");
                                number = recvINTArrayData();
                                problem.setText("Problem: " + number[0]  + " " + number[1] + " " + number[2] + " " + number[3]);
                                if(time == 0){
                                    alreadyAnswered = false;
                                    warning.setText("");
                                }
                                Thread.sleep(1000);
                            }
                            catch(IOException | InterruptedException e2){
                                System.out.println("Error in getProblem");
                            }
                        }
                        
                    }
                };

                Thread getScore = new Thread(){
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
                frame.add(info);
                frame.add(problem);
                frame.add(timeLeftLabel);
                frame.add(warning);
                frame.add(input);
                frame.add(submit);
                timer.start();
                getScore.start();


                try{
                    sendData("add,"+name);
                } catch (IOException e1){
                    System.out.println("Error in add (sendData)");
                }
                
                submit.addActionListener(e1 -> {   
                    String answer = input.getText();
                    boolean c = checkAns(answer);
                    if(alreadyAnswered){
                        warning.setText("     Already answered");
                    } else if(!c){
                        warning.setText("     Incorrect Answer");
                    }
                    if(c){                   
                        try {
                            sendData("correct,"+name);
                        } catch (IOException e2) {
                            System.out.println("Error IN submit btn");
                        }
                        warning.setText("     Correct Answer");
                        alreadyAnswered = true;
                    }
                    
                });

                frame.revalidate();
                frame.repaint();
            });
    
            frame.setVisible(true);
            
        }
        
    }

    public boolean checkAns(String answer){
        if (alreadyAnswered) {
            return false;
        }
        if(answer.length() > 7){
            return false;
        }
        clearCheckNumber();
        int sum = 0;
        answer = answer.trim();
        answer = clearWSpace(answer);
        int[] num = getNumber(answer);
        checkNumbers(number);
        for(int i : num){
            checkNumber[i]--;
            if (checkNumber[i] < 0) {
                return false;
            }
        }
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


    private void checkNumbers(int[] num){
        for(int i = 0; i < num.length; i++){
            checkNumber[num[i]]++;
        }
    }

    private void clearCheckNumber(){
        for(int i = 0; i < checkNumber.length; i++){
            checkNumber[i] = 0;
        }
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

    public int recvINTData() throws IOException {
        String receivedData = in.readLine();
        int res = Integer.parseInt(receivedData);
        return res;
    }

    public String[] recvStringArrayData() throws IOException {
        String receivedData = in.readLine();
        String[] res = null;
        res = receivedData.substring(1, receivedData.length() - 1).split(", ");
        return res;
    }

}

