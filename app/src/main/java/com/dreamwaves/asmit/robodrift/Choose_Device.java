package com.dreamwaves.asmit.robodrift;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class Choose_Device extends AppCompatActivity {


    public static BluetoothSocket bluetoothSocket=null;
    public ProgressBar progress;
    // public boolean isBluetoothConnected = false;
    public BluetoothAdapter myBluetooth = null;
    ListView listView;
    TextView textView;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public Set<BluetoothDevice> bluetoothDevices;
    public BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose__device);
        textView = (TextView) findViewById(R.id.selectText);
        listView = (ListView) findViewById(R.id.listView);
        progress = (ProgressBar) findViewById(R.id.cont_progress);
        selectDevice();
    }

    void selectDevice(){

        bluetoothDevices = bluetoothAdapter.getBondedDevices();
        if(bluetoothDevices.size()==0){
            textView.setTextColor(Color.RED);
            textView.setText("Sorry No any Paired Devices to display \n Please PAIR device from setting!");
        }
        ArrayList list = new ArrayList();
        for(BluetoothDevice b:bluetoothDevices) {
            list.add(b.getName()+" MAC:"+b.getAddress());
        }
        final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                Toast.makeText(getApplicationContext(),"Selected "+info ,Toast.LENGTH_SHORT).show();
                establishingConection(address);
            }



            public void establishingConection(String address){
                ConnectingToTheDevice connectingToTheDevice = new ConnectingToTheDevice();
                connectingToTheDevice.execute(new String[]{address});
            }

        });



    }
    class ConnectingToTheDevice extends AsyncTask<String,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String address = params[0];
            try {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                //connects to the device's address and checks if it's available
                BluetoothDevice device = myBluetooth.getRemoteDevice(address);
                bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                bluetoothSocket.connect();//start connection
            //    Toast.makeText(getApplicationContext(),"Connected to "+address,Toast.LENGTH_LONG).show();
                return true;
            } catch (IOException e) {
            //    Toast.makeText(getApplicationContext(),"Cannot Connect for some reasons",Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progress.setVisibility(View.INVISIBLE);
            if(aBoolean==false){
                textView.setTextColor(Color.RED);
                textView.setText("Sorry cannot connect!");
            }
            else {
                Intent intent = new Intent(Choose_Device.this,MainActivity.class);
                startActivity(intent);
            }
        }
    }

}


