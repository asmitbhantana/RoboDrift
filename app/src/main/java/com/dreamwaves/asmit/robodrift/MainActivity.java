package com.dreamwaves.asmit.robodrift;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    Button lbtn,rbtn,bbtn,fbtn;
    ImageButton choosebt;
    BluetoothAdapter adapter;
    TextView tv;
    ScrollView scrollView;
    SeekBar seekBar;
    boolean result;
    static String s;
    String t;
    private Handler repeatUpdateHandler = new Handler();
    private final long REPEAT_DELAY = 50;
    static int scrollc = 200;
    static int test = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lbtn = (Button) findViewById(R.id.leftbtn);
        rbtn = (Button) findViewById(R.id.rightbtn);
        fbtn = (Button) findViewById(R.id.frontbtn);
        bbtn = (Button) findViewById(R.id.backbtn);
        choosebt = (ImageButton) findViewById(R.id.chooseDevice);
        tv = (TextView) findViewById(R.id.sc_text);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        seekBar = (SeekBar) findViewById(R.id.seekBar5);

      //  this.requestWindowFeature(Window.FEATURE_NO_TITLE);

       //Click Event for the button
        scrollView.setSmoothScrollingEnabled(true);

        choosebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceInfo();
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                t = String.valueOf(progress);
                move();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t = "F";
                move();
              //  appendingToTV(":moving Forward "+result+":");
            }
        });

        fbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                t = "F";
                repeatUpdateHandler.removeCallbacks(new RepetitiveButtonClicker());
                repeatUpdateHandler.postAtFrontOfQueue(new RepetitiveButtonClicker());
                return false;
            }
        });

        fbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               if(event.getAction() == MotionEvent.ACTION_UP){
                   Log.e("","Helloooooo");
               }
                return false;
            }
        });

        lbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     result = move("L");
           //     appendingToTV(":moving Left "+result+":");
                t = "L";
                move();

            }
        });
        lbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                t = "L";
                repeatUpdateHandler.removeCallbacks(new RepetitiveButtonClicker());
                repeatUpdateHandler.postAtFrontOfQueue(new RepetitiveButtonClicker());
                Log.e(" TEST", String.valueOf(test));
                return false;
            }
        });


        bbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t = "B";
                move();
              //  appendingToTV(":moving Backward "+result+":");
            }
        });
        bbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                t = "B";
                repeatUpdateHandler.removeCallbacks(new RepetitiveButtonClicker());
                repeatUpdateHandler.postAtFrontOfQueue(new RepetitiveButtonClicker());
                return false;
            }
        });


        rbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t = "R";
                move();
              //  appendingToTV(":moving Right "+result+":");
            }
        });
        rbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                t = "R";
                repeatUpdateHandler.removeCallbacks(new RepetitiveButtonClicker());
                repeatUpdateHandler.postAtFrontOfQueue(new RepetitiveButtonClicker());
                return false;
            }
        });



    }
    void appendingToTV(String text){

        tv.append(text+"\n");
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0,scrollc);
                scrollc = scrollc+ 130;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        s = tv.getText().toString();
        Log.e("pasued","Paused"+s);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv.setText(s);
        Log.e("resumed","Reasumed"+s);
    }

    //Initilization OF adapter
    public boolean initBT(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter==null) {
            Toast.makeText(getApplicationContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return  false;
        }
        if(adapter.isEnabled()){
            Toast.makeText(getApplicationContext(),"Bluetooth is enabled",Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
                Toast.makeText(getApplicationContext(),"Please turn ON the Bluetooth!",Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    //Initilization getting all you want
    public void deviceInfo(){
        if(initBT())
        {

            Intent intent = new Intent(MainActivity.this, Choose_Device.class);
            startActivity(intent);
        }
        else
        {
            Intent startBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(startBluetooth, 1);
        }

    }

    void move(){
        if(Choose_Device.bluetoothSocket!=null){
            try {
                Choose_Device.bluetoothSocket.getOutputStream().write(t.toString().getBytes());
                tv.setTextColor(Color.GREEN);

            } catch (IOException e) {
                e.printStackTrace();
                tv.setTextColor(Color.RED);
                Log.e("No","No any BT Socket");

            }
        }else {
            tv.setTextColor(Color.RED);


        }
        appendingToTV(":moving "+t+":");
    }

/*
For handling the long click listener for buttons
 */

class RepetitiveButtonClicker implements Runnable{

    @Override
    public void run() {
        test++;

        move();
        repeatUpdateHandler.postDelayed(new RepetitiveButtonClicker(), REPEAT_DELAY);
    }
}

}
