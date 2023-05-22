package medical_server;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;

public class M{

    static int port = 1204;
    static ServerSocket server_socket;
    static Socket connection_socket;
    static InputStreamReader inputStream;
    static OutputStreamWriter outputStream;
    static BufferedReader reader;
    static BufferedWriter writer;
    static boolean listening = true;
    static String input, temp_msg, rate_msg, oxygen_msg, action;
    static double temp = 0, rate = 0, oxygen = 200;
    //Create and set up the frame  
    static JFrame frame = new JFrame("Medical Server");
    static JTextArea textArea;
    
    public static void main(String[] args) throws Exception {
        set_up_gui();
        try {
            // creating TCP server socket "welcoming socket"
            server_socket = new ServerSocket(port);
            // wait for incoming connection request
            while (listening) {
                // new connection socket returned by accept() when server recieve a request from the client  
                connection_socket = server_socket.accept();
                // setting up input/output streams and buffer reader/writer 
                connect();
                // keep connection while the client is sending
                input = reader.readLine();
                while ((input) != null) {
                    temp_msg = ""; rate_msg = ""; oxygen_msg = "";
                    // obtaining each of the 3 messages
                    if (input != null && input.contains("Temperature")) {
                        temp_msg = input;
                        input = reader.readLine();
                    }
                    if (input != null && input.contains("Heart")) {
                        rate_msg = input;
                        input = reader.readLine();
                    }
                    if (input != null && input.contains("Oxygen")) {
                        oxygen_msg = input;
                        input = reader.readLine();
                    }
                    // get the numbers representing temperature, heart rate, and oxygen saturation
                    extract_stat();
                    // decide on the action to be displayed
                    action = decide_action();
                    // display the approriate message with the decided action
                    display();
                }
                close_connection();
            }
        } catch (IOException e) {
        }
    }

    static void connect() throws IOException {
        // get connection_socket's input and output stream
        inputStream = new InputStreamReader(connection_socket.getInputStream());
        outputStream = new OutputStreamWriter(connection_socket.getOutputStream());
        // construct buffered reader and buffered writer to read/write from/to client
        reader = new BufferedReader(inputStream);
        writer = new BufferedWriter(outputStream);
    }

    static void extract_stat() {
        if (!temp_msg.equals("")) {
            temp = Double.parseDouble(temp_msg.substring(temp_msg.indexOf("high") + 4, temp_msg.length() - 1));
        }
        if (!rate_msg.equals("")) {
            rate = Double.parseDouble(rate_msg.substring(rate_msg.indexOf("normal") + 7, rate_msg.length() - 1));
        }
        if (!oxygen_msg.equals("")) {
            oxygen = Double.parseDouble(oxygen_msg.substring(oxygen_msg.indexOf("low") + 3, oxygen_msg.length() - 1));
        }
    }

    static String decide_action() {
        // decide the action according to the temperature, heart rate, and oxygen saturation
        if (temp >= 39 && rate >= 100 && oxygen <= 95) {
            return "Send an ambulance to the patient!";
        } else if (temp >= 38 && temp <= 38.9 && rate >= 95 && rate <= 98 && oxygen <= 80) {
            return "Call the patient's family!";
        } else {
            return "Warning, advise patient to make a checkup appointment!";
        }
    }
    static void set_up_gui(){
            // creat text area
        textArea = new JTextArea(
                ""
        );
        // customize the text area
        textArea.setFont(new Font("DialogInput", Font.PLAIN, 25));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(new Color(245, 255, 250));
        textArea.setForeground(new Color(105, 105, 105));
        textArea.setEditable(false);

        // creat a scroll pane to allow scrolling
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        //Add the text area to the frame
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        // set the frame size
        frame.setPreferredSize(new Dimension(700, 400));
  
        frame.pack();
        // set the frame location on the screen
        Point point = new Point(610-350, 340);
        frame.setLocation(point);   
    }
    
    // use gui to display the medical server output
    static void display() {
        frame.setVisible(true);
        // add to the text area
        textArea.append(
                " " + temp_msg + "\n " + rate_msg + "\n " + oxygen_msg + "\n\n ACTION: " + action + "\n\n\n"
        );
    }

    static void close_connection() throws Exception{
        connection_socket.close();   
        inputStream.close();
        outputStream.close();
        reader.close();
        writer.close();
    }
}
