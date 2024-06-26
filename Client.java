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

public class Client {
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String name;
    private boolean isFull;
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
                this.isFull = false;
                System.out.println("Connected to the server.");
                connect = true;
                out.println("isFull");
                String response = in.readLine();
                if (response.equals("true")) {
                    this.isFull = true;
                    connect = false;
                    System.out.println("Server is full. Try again later.");
                    socket.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Error: Unable to connect to the server.");
            connect = false;
        }
    }

    public void run() {
        if(this.isFull){
            JFrame frame = new JFrame("Game Full");
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            JLabel label = new JLabel("Server is full. Try again later.");
            Font labelFont = label.getFont();
            label.setFont(new Font(labelFont.getName(), Font.PLAIN, 20));
            JButton button = new JButton("Retry");
            button.setPreferredSize(new Dimension(100, 50));
            button.addActionListener(e -> {
                Client c = new Client(5000, "localhost");
                c.run();
                frame.dispose();
            });
            frame.add(label);
            frame.add(button);
            frame.setVisible(true);
        }
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
            textField.setSize(new Dimension(100, 50));
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

                try {
                    sendData("getProblem");
                    number = recvINTArrayData();
                    problem.setText("Problem: " + number[0] + " " + number[1] + " " + number[2] + " " + number[3]);
                } catch (IOException e2) {
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
                input.setSize(new Dimension(50, 40));

                JButton submit = new JButton("Submit");
                submit.setPreferredSize(new Dimension(100, 50));

                Thread timer = new Thread() {
                    public void run() {

                        while (true) {
                            try {
                                sendData("time");
                                time = recvINTData();
                                timeLeftLabel.setText("Time: " + time);
                            } catch (IOException e1) {
                                System.out.println("Error in time");
                            }
                            try {
                                sendData("getProblem");
                                number = recvINTArrayData();
                                problem.setText(
                                        "Problem: " + number[0] + " " + number[1] + " " + number[2] + " " + number[3]);
                                if (time == 0) {
                                    alreadyAnswered = false;
                                    warning.setText("");
                                }
                                Thread.sleep(1000);
                            } catch (IOException | InterruptedException e2) {
                                System.out.println("Error in getProblem");
                            }
                        }

                    }
                };

                Thread getScore = new Thread() {
                    public void run() {
                        while (true) {
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
                                for (int i=0;i<4;i++){
                                    int s = data[i].length();
                                    int check = data[i].charAt(s-8) - '0';
                                    if (check >= 3){
                                        JOptionPane.showMessageDialog(frame, "Player " + data[i].substring(0, s-10) + " wins");
                                        System.exit(0);
                                        sendData("win");
                                    }
                                }
                                
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

                try {
                    sendData("add," + name);
                } catch (IOException e1) {
                    System.out.println("Error in add (sendData)");
                }

                submit.addActionListener(e1 -> {
                    String answer = input.getText();
                    boolean c = checkAns(answer);
                    if (alreadyAnswered) {
                        warning.setText("     Already answered");
                    } else if (!c) {
                        warning.setText("     Incorrect Answer");
                    }
                    if (c) {
                        try {
                            sendData("correct," + name);
                        } catch (IOException e2) {
                            System.out.println("Error IN submit btn");
                        }
                        warning.setText("     Correct Answer");
                        alreadyAnswered = true;
                        clearInputField(input);
                    }

                });

                frame.revalidate();
                frame.repaint();
            });

            frame.setVisible(true);

        }

    }

    public static boolean checkBracket(String x) {
        for (int i = 0; i < x.length(); i++) {
            char c = x.charAt(i);
            if (c == '(' || c == ')') {
                return true;
            }
        }
        return false;
    }

    public boolean checkAns(String answer) {
        // return true;
        if (alreadyAnswered) {
            return false;
        }
        clearCheckNumber();
        int[] num = getNumber(answer);
        checkNumbers(number);
        for (int i : num) {
            checkNumber[i]--;
            if (checkNumber[i] < 0) {
                return false;
            }
        }
        answer = clearWSpace(answer);
        if (checkBracket(answer)) {
            return checkAnsWithBrackets(answer);
        } else {
            return checkSimpleAns(answer);
        }
    }

    private boolean checkSimpleAns(String answer) {
        int sum = 0;
        int[] num = getNumber(answer);
        for (int i = 1; i < answer.length(); i = i + 2) {
            char c = answer.charAt(i);
            if (c < '0' || c > '9') {
                switch (c) {
                    case '+':
                        if (i == 1) {
                            sum = num[0] + num[1];
                        }
                        if (i == 3) {
                            sum = sum + num[2];
                        }
                        if (i == 5) {
                            sum = sum + num[3];
                        }
                        break;
                    case '-':
                        if (i == 1) {

                            sum = num[0] - num[1];
                        }
                        if (i == 3) {
                            sum = sum - num[2];
                        }
                        if (i == 5) {
                            sum = sum - num[3];
                        }
                        break;
                    case '*':
                        if (i == 1) {
                            sum = num[0] * num[1];
                        }
                        if (i == 3) {
                            sum = sum * num[2];
                        }
                        if (i == 5) {
                            sum = sum * num[3];
                        }
                        break;
                    case '/':
                        if (i == 1) {
                            sum = num[0] / num[1];
                        }
                        if (i == 3) {
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

    private boolean checkAnsWithBrackets(String answer) {
        while (answer.contains("(")) {
            int closeIndex = answer.indexOf(')');
            int openIndex = answer.lastIndexOf('(', closeIndex);
            String subExpr = answer.substring(openIndex + 1, closeIndex);
            int subResult = evalNumber(subExpr);
            answer = answer.substring(0, openIndex) + subResult + answer.substring(closeIndex + 1);
        }
        return evalNumber(answer) == 24;
    }

    private int evalNumber(String expr) {
        int[] num = getNumber(expr);
        int sum = num[0];
        for (int i = 1; i < expr.length(); i += 2) {
            char op = expr.charAt(i);
            int nextNum = num[i / 2 + 1];
            switch (op) {
                case '+':
                    sum += nextNum;
                    break;
                case '-':
                    sum -= nextNum;
                    break;
                case '*':
                    sum *= nextNum;
                    break;
                case '/':
                    sum /= nextNum;
                    break;
                default:
                    break;
            }
        }
        return sum;
    }

    private String clearWSpace(String str) {
        return str.replaceAll(" ", "");
    }

    private int[] getNumber(String answer) {
        int[] res = new int[4];
        int index = 0;
        for (int i = 0; i < answer.length(); i++) {
            char c = answer.charAt(i);
            if (c < '0' || c > '9') {
                continue;
            }
            int num = c - '0';
            res[index] = num;
            index++;
        }
        return res;
    }

    private void checkNumbers(int[] num) {
        for (int i = 0; i < num.length; i++) {
            checkNumber[num[i]]++;
        }
    }

    private void clearCheckNumber() {
        for (int i = 0; i < checkNumber.length; i++) {
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
        String[] strArray = receivedData.substring(1, receivedData.length() - 1).split(", ");
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

    public String recvStringData() throws IOException {
        String receivedData = in.readLine();
        return receivedData;
    }

    public void clearInputField(JTextField inputField) {
        inputField.setText("");
    }
}