package com.example.jpuente.bridgeswitchlive;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import android.widget.Toast;
import java.io.IOException;

import static com.example.jpuente.bridgeswitchlive.MainActivity.client_socket;


public class ControlActivity extends AppCompatActivity {


    LinearLayout userInterface;
    public static OutputStream mOutputStream;
    public static InputStream mInputStream;
    ProgressBar loadingSpinner;
    ImageView errorImageView;
    TextView errorMessage;
    TextView powerButtonStateTextView;
    ImageView powerButton;
    char powerButtonState;
    SeekBar speedControlSeekbar;
    int pwmDutyCycle;
    TextView speedTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        setTitle("Motor Control");



        userInterface = (LinearLayout) findViewById(R.id.user_interface);
        powerButton = (ImageView) findViewById(R.id.power_button);

        errorMessage = (TextView) findViewById(R.id.empty_text_view);

        loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        errorImageView = (ImageView) findViewById(R.id.error_connection);

        powerButtonStateTextView = (TextView) findViewById(R.id.power_button_state);

        speedControlSeekbar = (SeekBar) findViewById(R.id.seekBar);

        speedTextView = (TextView) findViewById(R.id.speedText);




        powerButtonState = '0';
        //Test button to send data to the server
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Try to send some data
                if (client_socket.isConnected()) {

                    try {
                        mOutputStream = client_socket.getOutputStream();

                        if(powerButtonState == '0'){

                            writeBytes("201".getBytes());
                            powerButtonState = '1';
                            Toast.makeText(getApplicationContext(), "Power ON", Toast.LENGTH_SHORT).show();
                            powerButtonStateTextView.setText("ON");

                        }
                        else if(powerButtonState == '1') {

                            mOutputStream.write("200".getBytes());
                            powerButtonState = '0';
                            Toast.makeText(getApplicationContext(), "Power OFF", Toast.LENGTH_SHORT).show();
                            powerButtonStateTextView.setText("OFF");

                        }


                    } catch (IOException e) {

                        e.printStackTrace();
                    }


                }else
                    Toast.makeText(getApplicationContext(), "Client is not connected", Toast.LENGTH_LONG).show();
            }
        });


        speedControlSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pwmDutyCycle = progress;

                //send duty cycle update
                if(client_socket.isConnected()){

                    try {
                        mOutputStream = client_socket.getOutputStream();

                        writeBytes(String.valueOf(progress).getBytes());

                        speedTextView.setText(String.valueOf(progress));

                    }catch (IOException e){

                        e.printStackTrace();
                    }
                }

            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Toast.makeText(getApplicationContext(), "Motor speed is set to: " + pwmDutyCycle + "%",Toast.LENGTH_SHORT).show();
            }
        });
    }


    //method for writing stream of bytes to OutputStream
    public static void writeBytes(byte[] bytes){

        try {
            mOutputStream.write(bytes);
        }catch (IOException e){
            e.printStackTrace();
        }


    }


}


