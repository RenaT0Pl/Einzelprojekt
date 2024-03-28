package com.example.einzelprojekt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {
    private EditText editTextNumber;
    private TextView textView2;
    private Button button, button2;
    String result = "", art = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Test
        editTextNumber = findViewById(R.id.editTextNumber);
        textView2 = findViewById(R.id.textView2);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button2.setVisibility(View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String matrikelnummer = String.valueOf(editTextNumber.getText());
                TCPConnection(matrikelnummer);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String zahl = String.valueOf(editTextNumber.getText());
                int res = 0, loop = 0;
                for(int i = zahl.length()-1; i>=0; i--){
                    if(loop % 2 == 0){
                        res += Character.getNumericValue(zahl.charAt(loop));
                    }
                    else{
                        res -= Character.getNumericValue(zahl.charAt(loop));
                    }
                    loop++;
                }
                if(res % 2 == 0){
                        art = "gerade";
                    }
                else {
                    art = "ungerade";
                }
                textView2.setText("Erg: " + res +"\nArt: " + art);
            }
        });
    }
    public void TCPConnection(String... params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Server-Daten
                    String serverAddress = "se2-submission.aau.at";
                    int serverPort = 20080;

                    // Socket erstellen
                    Socket socket = new Socket(serverAddress, serverPort);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Matrikelnummer senden
                    dos.writeBytes(params[0] + "\n");
                    dos.flush(); //warten

                    // Antwort vom Server empfangen
                    result = br.readLine();

                    // Socket schließen
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!result.equals("Dies ist keine gueltige Matrikelnummer") && !result.equals("Antwort vom Server")){
                            button2.setVisibility(View.VISIBLE);
                        }
                        else{
                            button2.setVisibility(View.INVISIBLE);
                        }
                        textView2.setText(result);
                    }
                });
            }
        }).start();
    }
}