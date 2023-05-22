package medical;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;

public class Medical {

    static int port = 1204;
    static ServerSocket server_socket;
    static Socket connection_socket;
    static InputStreamReader inputStream;
    static OutputStreamWriter outputStream;
    static BufferedReader reader;
    static BufferedWriter writer;
    static boolean listening = true;
    static String input, date, time, temp_msg, rate_msg, oxygen_msg, action;
    static double temp, rate, oxygen;
    static JFrame frame;
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
                    date = input;
                    time = reader.readLine();
                    temp = Double.parseDouble(reader.readLine());
                    rate = Double.parseDouble(reader.readLine());
                    oxygen = Double.parseDouble(reader.readLine());
                    action = "";
                    // display appropriate message for readings
                    process_and_display_reading();
                    // decide on the action to be displayed
                    action = decide_action();
                    // display action
                    display(action);
                    input = reader.readLine();
                }
                close_connection();
            }
        } catch (IOException e) {
        }
    }

    static void connect() throws IOException {
        // get connection_socket's input and output stream
        inputStream = new InputStreamReader(connection_socket.getInputStream());
        // construct buffered reader and buffered writer to read/write from/to client
        reader = new BufferedReader(inputStream);
    }

    static void process_and_display_reading() {
        if (temp >= 38) {
            temp_msg = "At date :" + date + ",time " + time + ", Temperature is high " + temp + ".\n";
            display(temp_msg);
        }
        if (rate >= 100 || rate <= 60) {
            if (rate >= 100) {
                rate_msg = "At date :" + date + ",time " + time + ", Heart rate is above normal " + rate + ".\n";
            } else {
                rate_msg = "At date :" + date + ",time " + time + ", Heart rate is below normal " + rate + ".\n";
            }
            display(rate_msg);
        }
        if (oxygen < 75) {
            oxygen_msg = "At date :" + date + ",time " + time + ", Oxygen saturation is low " + oxygen + ".\n";
            display(oxygen_msg);
        }
    }

    static String decide_action() {
        // decide the action according to the temperature, heart rate, and oxygen saturation
        if (temp >= 39 && rate >= 100 && oxygen <= 95) {
            return "\n\n ACTION: Send an ambulance to the patient!\n\n\n";
        } else if (temp >= 38 && temp <= 38.9 && rate >= 95 && rate <= 98 && oxygen <= 80) {
            return "\n\n ACTION: Call the patient's family!\n\n\n";
        } else {
            return "\n\n ACTION: Warning, advise patient to make a checkup appointment!\n\n\n";
        }
    }

    // use gui to display the medical server output
    static void display(String msg) {
        frame.setVisible(true);
        // add to the text area
        textArea.append(
                msg
        );
    }

    // set up gui frame and textArea
    static void set_up_gui() {
        // create the frame object
        frame = new JFrame("Medical Server");

        // create text area
        textArea = new JTextArea(
                ""
        );
        // customize the text area
        textArea.setFont(new Font("DialogInput", Font.PLAIN, 25));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(new Color(245,218,223));
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
        Point point = new Point(610 - 350, 340 + 200);
        frame.setLocation(point);
        frame.setAlwaysOnTop(true);
    }

    static void close_connection() throws Exception {
        connection_socket.close();
        inputStream.close();
        reader.close();
    }
}
