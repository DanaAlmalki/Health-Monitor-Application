package personal_server;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Personal {

    static int port = 2000;
    static Socket connectionSocket;
    static Socket personal2MedicalConnection;
    static InputStreamReader SensorOutput;
    static OutputStreamWriter personal2Medical;
    static BufferedReader reader;
    static BufferedWriter writer;
    static double temperature;
    static double heartRate;
    static int oxygenSaturation;
    static String date, temp;
    static String time;
    static String tempCaseMsg, heartCaseMsg, oxgCaseMsg;
    static JFrame frame = new JFrame("Personal Server");
    static JTextArea textArea;

    public static void main(String[] args) throws Exception {
        set_up_gui();
        // personal server
        ServerSocket startSocket = new ServerSocket(port);
        // infinite loop to accept connection request from sensor
        while (true) {
            // personal connection with the sensor app
            connectionSocket = startSocket.accept(); //Handshaking 

            // data steam obj to make the server read client input through connection socket 
            SensorOutput = new InputStreamReader(connectionSocket.getInputStream());

            // connection socket with medical server
            personal2MedicalConnection = new Socket("localhost", 1204);

            //data steam obj to make the server able to send masseges to thr client
            personal2Medical = new OutputStreamWriter(personal2MedicalConnection.getOutputStream());

            // construct buffered reader and buffered writer
            reader = new BufferedReader(SensorOutput);
            writer = new BufferedWriter(personal2Medical);

            while ((temp = reader.readLine()) != null) {
                //take a value from the client
                temperature = Double.valueOf(temp);
                date = reader.readLine();
                time = reader.readLine();
                tempCaseMsg = checkTemp(date, time, temperature);

                heartRate = Double.valueOf(reader.readLine());
                date = reader.readLine();
                time = reader.readLine();
                heartCaseMsg = checkHeart(date, time, heartRate);

                oxygenSaturation = Integer.valueOf(reader.readLine());
                date = reader.readLine();
                time = reader.readLine();
                oxgCaseMsg = checkOxygen(date, time, oxygenSaturation);
                //condition to stop?? all the input in string??

                System.out.println(tempCaseMsg);
                System.out.println(heartCaseMsg);
                System.out.println(oxgCaseMsg);
                display();
                // now I dont get how the client part will work and how to use outsteam
            }//inner while loop end 

            // close connection.
            close_connection();
        }

    }//main end

    public static String checkTemp(String date, String time, double temperature) throws IOException {
        if (temperature >= 38) {
            String msg = "At date :" + date + ",time " + time + ", Temperature is high " + temperature + ".";
            writer.write(msg);
            writer.newLine();
            writer.flush();
            return msg + " An alert message is sent to the Medical Server";
        } else {
            return "At date :" + date + ",time " + time + ", Temperature is normal " + temperature + ".";
        }
    }//method end

    //------------------------------------------------------------------------------------------------------------   
    public static String checkHeart(String date, String time, double heartRate) throws IOException {
        if (heartRate >= 100) {
            String msg = "At date :" + date + ",time " + time + ", Heart rate is above normal " + heartRate + ".";
            writer.write(msg);
            writer.newLine();
            writer.flush();
            return msg + " An alert message is sent to the Medical Server";

        } else if (heartRate <= 60) {
            String msg = "At date :" + date + ",time " + time + ", Heart rate is below normal " + heartRate + ".";
            writer.write(msg);
            writer.newLine();
            writer.flush();
            return msg + " An alert message is sent to the Medical Server";

        } else {
            return "At date :" + date + ",time " + time + ", Heart rate is normal " + heartRate + ".";
        }
    }//method end

    //------------------------------------------------------------------------------------------------------------   
    public static String checkOxygen(String date, String time, int oxygenSaturation) throws IOException {
        if (oxygenSaturation <= 75) {
            String msg = "At date :" + date + ",time " + time + ", Oxygen saturation is low " + oxygenSaturation + ".";
            writer.write(msg);
            writer.newLine();
            writer.flush();
            return msg + " An alert message is sent to the Medical Server";
        } else {
            return "At date :" + date + ",time " + time + ", Oxygen saturation is normal " + oxygenSaturation + ".";
        }
    }//method end

    static void close_connection() throws Exception {
        connectionSocket.close();
        personal2MedicalConnection.close();
        reader.close();
        writer.close();
        personal2Medical.close();
        SensorOutput.close();
    }

    static void set_up_gui() {
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
        Point point = new Point(610 + 350, 340);
        frame.setLocation(point); 
    }

    // use gui to display the medical server output
    static void display() {
        frame.setVisible(true);
        // add to the text area
        textArea.append(
                " " + tempCaseMsg + "\n " + heartCaseMsg + "\n " + oxgCaseMsg + "\n\n\n"
        );
    }
}
