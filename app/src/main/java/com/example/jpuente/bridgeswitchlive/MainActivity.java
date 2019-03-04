package com.example.jpuente.bridgeswitchlive;

import android.support.v7.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice = null;
    public static BluetoothSocket client_socket;
    UUID uuid = UUID.fromString("9ba4c432-79d3-4d2c-a1ec-134aefb1f94b");
    ImageView errorImageView;
    TextView errorMessage;
    ProgressBar loadingSpinner;
    RelativeLayout mainUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorImageView = (ImageView) findViewById(R.id.error_connection);
        errorMessage = (TextView) findViewById(R.id.empty_text_view);
        loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        mainUI = (RelativeLayout) findViewById(R.id.main_ui);

        //Set up bluetooth
        setupBluetooth();

        //Get the remote bluetooth device (raspberry pi)
        getRemoteBluetoothDevice();

        //execute connectionAsyncTask
        new ConnectionAsyncTask().execute();

    }


    //method for starting the control activity
    public void startControlActivity(View view){

    Intent intent = new Intent(this, ControlActivity.class);

     startActivity(intent);

    }


    //method for starting the status activity
    public void startStatusActivity(View view){

        Intent intent = new Intent(this, StatusActivity.class);

        startActivity(intent);

    }



    public void setupBluetooth()
    {

        //Get the device bluetooth adapater
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Enable bluetooth
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }

    }


    public void getRemoteBluetoothDevice(){

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){

            for(BluetoothDevice device : pairedDevices){

                if(device.getName().equals("raspberrypi")){

                    mBluetoothDevice = device;

                    Log.i("MotorController", device.getName());
                    break;
                }
            }


        }
    }


    public class ConnectionAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            //Try to connect to the server
            try {

                //create a bluetooth client socket to start a secure outgoing connection to the remote bluetooth device
                /*
                 * createRfcommSocketToServiceRecord takes the UUID and uses SDP to decide what radio channel to use in the connection
                 * It also checks to make sure that a server is listening on the remote end point with the same UUID: most reliable way to get a connection
                 * */
                client_socket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);


                //initiate an outgoing connection
                if(!client_socket.isConnected()){

                    client_socket.connect();

                }

            }

            catch (IOException e){

                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            loadingSpinner.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadingSpinner.setVisibility(View.INVISIBLE);

            if(client_socket.isConnected()){
                Toast.makeText(getApplicationContext(), "Connected to the server", Toast.LENGTH_LONG).show();

                //show the main UI
                mainUI.setVisibility(View.VISIBLE);

            }else{

                Toast.makeText(getApplicationContext(), "Not connected, make sure that the server is up", Toast.LENGTH_LONG).show();
               errorImageView.setVisibility(View.VISIBLE);
               errorMessage.setVisibility(View.VISIBLE);
            }

        }
    }





}
