package com.example.jpuente.bridgeswitchlive;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static com.example.jpuente.bridgeswitchlive.ControlActivity.mInputStream;
import static com.example.jpuente.bridgeswitchlive.ControlActivity.mOutputStream;
import static com.example.jpuente.bridgeswitchlive.ControlActivity.writeBytes;
import static com.example.jpuente.bridgeswitchlive.MainActivity.client_socket;

public class StatusActivity extends AppCompatActivity {

    //buffer variable that will store the incoming received data from the Bluetooth server
    byte[] buffer = new byte[256];

    int bytes;
    int i;


    TextView firstDeviceTextView;
    TextView secondDeviceTextView;
    TextView thirdDeviceTextView;
    BluetoothAsyncTask mBluetoothAsyncTask;

    ArrayList<String> mArrayList;
    ArrayList<String> mFirstDeviceList;
    ArrayList<String> mSecondDeviceList;
    ArrayList<String> mThirdDeviceList;

    ArrayAdapter<String> arrayAdapter1;
    ArrayAdapter<String> arrayAdapter2;
    ArrayAdapter<String> arrayAdapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        setTitle("Device Status");


        firstDeviceTextView = findViewById(R.id.device_1);
        secondDeviceTextView = findViewById(R.id.device_2);
        thirdDeviceTextView = findViewById(R.id.device_3);

        ListView firstDeviceListView = (ListView) findViewById(R.id.list_device_1);
        ListView secondDeviceListView = (ListView) findViewById(R.id.list_device_2);
        ListView thirdDeviceListView = (ListView) findViewById(R.id.list_device_3);

        mArrayList = new ArrayList<>();
        mFirstDeviceList = new ArrayList<>();
        mSecondDeviceList = new ArrayList<>();
        mThirdDeviceList = new ArrayList<>();


        //create array adapters
        arrayAdapter1 = new ArrayAdapter<String>(this,R.layout.simple_list_item_1,mFirstDeviceList);
        arrayAdapter2 = new ArrayAdapter<String>(this,R.layout.simple_list_item_1,mSecondDeviceList);
        arrayAdapter3 = new ArrayAdapter<String>(this,R.layout.simple_list_item_1,mThirdDeviceList);

        firstDeviceListView.setAdapter(arrayAdapter1);
        secondDeviceListView.setAdapter(arrayAdapter2);
        thirdDeviceListView.setAdapter(arrayAdapter3);

        //execute the BluetoothAsyncTask
        mBluetoothAsyncTask = new BluetoothAsyncTask();
    }


    @Override
    protected void onStop() {
        super.onStop();

        mBluetoothAsyncTask.cancel(true);

    }



    @Override
    protected void onStart() {
        super.onStart();


        mBluetoothAsyncTask.execute();
    }



    public void askBridgeSwitch(View view){

        //Try to send some data
        if (client_socket.isConnected()) {

            try {
                mOutputStream = client_socket.getOutputStream();

                    writeBytes("203".getBytes());
                    Toast.makeText(getApplicationContext(), "Status query ...", Toast.LENGTH_SHORT).show();

                //TO-DO
                //empty fault status array contents
                mArrayList.clear();

                //reset count variable
                i =0;


            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        else{

            Toast.makeText(getApplicationContext(), "Client is not connected", Toast.LENGTH_LONG).show();
        }
    }



    public class BluetoothAsyncTask extends AsyncTask<Void, String, Void>{


        @Override
        protected Void doInBackground(Void... voids) {



            //keep looping to listen for received messages from the Bluetooth server
         while (true) {


             try {


                    mInputStream = client_socket.getInputStream();

                    bytes = mInputStream.read(buffer);

                    String rcv_data = new String(buffer, 0, bytes);

                    publishProgress(rcv_data);

                 if (isCancelled()) break;

             } catch (IOException e) {

                 e.printStackTrace();

                 Toast.makeText(getApplicationContext(), "Exception in receiving data", Toast.LENGTH_LONG).show();
             }


         }

         return null;

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);


            //update the status text view
            //statusTextView.setText(values[0]);


                mArrayList.add(values[0]);

            i++;

            if(i==3){

                try {
                    firstDeviceTextView.setText(mArrayList.get(0));

                    secondDeviceTextView.setText(mArrayList.get(1));

                    thirdDeviceTextView.setText(mArrayList.get(2));

                    int j=0;
                    for(String s: mArrayList){

                        Scanner scanner = new Scanner(s);

                        ProcessFault processFault = new ProcessFault();

                        String x = scanner.next();
                        String y = x.replaceAll("[^0-9]","");
                        String s1;
                        String s2;
                        String s3;

                        if(j==0) {

                            int first_fault = Integer.parseInt(y);

                            System.out.println(first_fault);

                            if(first_fault == 1){


                                s1  = "00000001";

                                System.out.println("First fault: " + "00000001");
                            }else {


                                String tfault = Integer.toBinaryString(first_fault);

                                System.out.println("First fault: " + tfault);


                                s1 = tfault;
                            }

                            //convert received fault string to fault status word format
                            s1 = invertStringCharacters(s1);

                            firstDeviceTextView.setText(s1);

                            //process device 1 fault status
                            mFirstDeviceList = processFault.updateArrayList(s1);

                            //update device 1 UI display
                            arrayAdapter1.notifyDataSetChanged();



                            j++;

                        }
                        else if(j ==1){

                            int second_fault = Integer.parseInt(y);

                            System.out.println(second_fault);

                            if(second_fault == 1){

                                s2  = "00000001";

                                System.out.println("Second fault: " + "00000001");

                            }else {


                                String tfault = Integer.toBinaryString(second_fault);

                                System.out.println("Second fault: " + tfault);


                               s2 = tfault;
                            }

                            //convert received fault string to fault status word format
                            s2 = invertStringCharacters(s2);

                            secondDeviceTextView.setText(s2);

                            //process device 2 fault status
                            mSecondDeviceList = processFault.updateArrayList(s2);

                            //update device 2 UI display
                            arrayAdapter2.notifyDataSetChanged();

                            j++;

                        }

                        else if(j ==2){

                            int third_fault = Integer.parseInt(y);

                            System.out.println(third_fault);

                            if(third_fault == 1){

                                s3  = "00000001";

                                System.out.println("Third fault: " + "00000001");

                            }else {


                                String tfault = Integer.toBinaryString(third_fault);

                                System.out.println("Third fault: " + tfault);


                               s3 = tfault;
                            }

                            //convert received fault string to fault status word format
                            s3 = invertStringCharacters(s3);

                            secondDeviceTextView.setText(s3);

                            //process device 3 fault status
                            mThirdDeviceList = processFault.updateArrayList(s3);

                            //update device 3 UI display
                            arrayAdapter3.notifyDataSetChanged();


                            j=0;

                        }

                    }

                }catch (IndexOutOfBoundsException e){

                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(),"Index out of bounds exception", Toast.LENGTH_SHORT).show();
                }

            }


        }

    }


    //convert received fault string to fault status word format
    public String invertStringCharacters(String s){

        char[] c1 = {s.charAt(7),s.charAt(6),s.charAt(5),s.charAt(4),s.charAt(3),s.charAt(2),s.charAt(1),s.charAt(0)};

        String new_s = new String(c1);

        return new_s;

    }


}




