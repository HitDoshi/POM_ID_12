package com.example.blutoothled;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private long timeCountInMilliSeconds ;
    private long timeCountInMilliSeconds2 ;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    private TextView editTextMinute;
    private TextView textViewTime;

    private ImageView imageViewReset;
    private Button imageViewStartStop;
    private CountDownTimer countDownTimer;
    private CountDownTimer countDownTimer2;
    int h1=0,m1=0,s1=0;
    int h2=0,m2=0,s2=0;

    private ProgressBar progressBarCircle2;
    private TextView editTextMinute2;
    private TextView textViewTime2;

    private int mMaxChars = 50000;//Default//change this to string..........
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    private ReadInput mReadThread = null;
    private BluetoothDevice mDevice;
    private BluetoothSocket mBTSocket;

        private ProgressDialog progressDialog;
    private ProgressDialog progressDialog2;

    private ListView listView;
    private BluetoothAdapter mBTAdapter;
    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable
    private static final int SETTINGS = 20;
    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int mBufferSize = 50000; //Default
    public static final String DEVICE_EXTRA = "com.example.lightcontrol.SOCKET";
    public static final String DEVICE_UUID = "com.example.lightcontrol.uuid";
    private static final String DEVICE_LIST = "com.example.lightcontrol.devicelist";
    private static final String DEVICE_LIST_SELECTED = "com.example.lightcontrol.devicelistselected";
    public static final String BUFFER_SIZE = "com.example.lightcontrol.buffersize";
    private static final String TAG = "BlueTest5-MainActivity";
    int temp=0;


    final static String on="92";//on 50 LED1 = 27
    final static String off="79";//off 55

    final static String on2="90";//on 48 LED2 = 25
    final static String off2="75";//off 53

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Create");

        //Customize the ActionBar
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(R.string.app_name);
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);

        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        //initListeners();

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();

        editTextMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
                d.setTitle("Select Timer For T1");
                d.setMessage("00:00:10 To 24:00:00");
                d.setView(dialogView);
                NumberPicker hour1 = (NumberPicker) dialogView.findViewById(R.id.hour1);
                hour1.setMinValue(0);
                hour1.setMaxValue(23);
                hour1.setWrapSelectorWheel(true);
                hour1.setValue(h1);

                NumberPicker min1 = (NumberPicker) dialogView.findViewById(R.id.min1);
                min1.setMinValue(0);
                min1.setMaxValue(59);
                min1.setWrapSelectorWheel(true);
                min1.setValue(m1);


                NumberPicker sec1 = (NumberPicker) dialogView.findViewById(R.id.sec1);

                sec1.setMinValue(0);
                sec1.setMaxValue(59);
                sec1 .setWrapSelectorWheel(true);
                sec1.setValue(s1);


                hour1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        //Log.d("HI", "onValueChange: ");
                    }
                });

                min1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        //Log.d("HI", "onValueChange: ");
                    }
                });

                sec1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        //Log.d("HI", "onValueChange: ");
                    }
                });

                hour1.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        return String.format("%02d", i);
                    }
                });

                min1.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        return String.format("%02d", i);
                    }
                });

                sec1.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        return String.format("%02d", i);
                    }
                });

                d.setPositiveButton("SET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       // Log.d("HIIII", "onClick: " + hour1.getValue());

                        String hms = String.format("T1:- %02d:%02d:%02d",hour1.getValue(),min1.getValue(),sec1.getValue());
                        String hms2 = String.format("%02d:%02d:%02d",hour1.getValue(),min1.getValue(),sec1.getValue());
                        editTextMinute.setText(hms);
                        textViewTime.setText(hms2);
                        h1  = hour1.getValue();
                        m1  = min1.getValue();
                        s1  = sec1.getValue();
                        //editTextMinute.setText(hour1.getValue() + ":" + min1.getValue() + ":" + sec1.getValue());
                    }
                });
                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = d.create();
                alertDialog.show();
            }
        });


        editTextMinute2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
                d.setTitle("Select Timer For T2");
                d.setMessage("00:00:10 To 24:00:00");
                d.setView(dialogView);
                NumberPicker hour2 = (NumberPicker) dialogView.findViewById(R.id.hour1);
                hour2.setMinValue(0);
                hour2.setMaxValue(23);
                hour2.setWrapSelectorWheel(true);
                hour2.setValue(h2);


                NumberPicker min2 = (NumberPicker) dialogView.findViewById(R.id.min1);
                min2.setMinValue(0);
                min2.setMaxValue(59);
                min2.setWrapSelectorWheel(true);
                min2.setValue(m2);


                NumberPicker sec2 = (NumberPicker) dialogView.findViewById(R.id.sec1);

                sec2.setMinValue(0);
                sec2.setMaxValue(59);
                sec2 .setWrapSelectorWheel(true);
                sec2.setValue(s2);

                hour2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        //Log.d("HI", "onValueChange: ");
                    }
                });

                min2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        //Log.d("HI", "onValueChange: ");
                    }
                });

                sec2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        //Log.d("HI", "onValueChange: ");
                    }
                });

                hour2.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        return String.format("%02d", i);
                    }
                });

                min2.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        return String.format("%02d", i);
                    }
                });

                sec2.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        return String.format("%02d", i);
                    }
                });

                d.setPositiveButton("SET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Log.d("HIIII", "onClick: " + hour1.getValue());

                        String hms = String.format("T2:- %02d:%02d:%02d",hour2.getValue(),min2.getValue(),sec2.getValue());
                        String hms2 = String.format("%02d:%02d:%02d",hour2.getValue(),min2.getValue(),sec2.getValue());
                        editTextMinute2.setText(hms);
                        textViewTime2.setText(hms2);
                        h2  = hour2.getValue();
                        m2  = min2.getValue();
                        s2  = sec2.getValue();
                        //editTextMinute.setText(hour1.getValue() + ":" + min1.getValue() + ":" + sec1.getValue());
                    }
                });
                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = d.create();
                alertDialog.show();
            }
        });


        imageViewStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( ((h1 * 60 * 60 * 1000) + (m1 * 60 * 1000) + (s1 * 1000)) >= 10000  &&
                        ((h2 * 60 * 60 * 1000) + (m2 * 60 * 1000) + (s2 * 1000)) >= 10000 ) {
                    // fetching value from edit text and type cast to integer

                    //startStop();
                    setTimerValues();
                    // call to initialize the progress bar values
                    setProgressBarValues();
                    setProgressBar3();

                    if (mBTAdapter == null) {
                        //Toast.makeText(getApplicationContext(), "Turn On Mobile Bluetooth", Toast.LENGTH_SHORT).show();
                        showErrorDialog(R.string.bluetoothon);
                    } else if (!mBTAdapter.isEnabled()) {
                        Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBT, BT_ENABLE_REQUEST);
                    } else {
                        Set<BluetoothDevice> bt = mBTAdapter.getBondedDevices();

                        if (bt.size() > 0) {

                            if (!(mBTSocket != null && mIsBluetoothConnected)) {

                                for (BluetoothDevice device : bt) {
                                    if (device.getName().equals("ESP32_LED_Control--2")) {
                                        temp = 1;
                                        //Toast.makeText(getApplicationContext(), "Ya", Toast.LENGTH_LONG).show();


                                        mDevice = device;
                                        mMaxChars = mBufferSize;

                                        Log.d(TAG, "Ready");

                                        if (mBTSocket == null || !mIsBluetoothConnected) {
                                            new ConnectBT().execute();
                                        }

                                        break;

                                    }
                                }

                                if(temp == 0)
                                {
                                    //Toast.makeText(getApplicationContext(), "No Device Found , Please Pair Device in Bluetooth", Toast.LENGTH_SHORT).show();
                                    showErrorDialog(R.string.pair);
                                }
                                temp = 0 ;
                            } else {
                                startCountDownTimer();
                            }
                        }
                    }

                }
                else {
                    // toast message to fill edit text
                    Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_ENABLE_REQUEST:
                if (resultCode == RESULT_OK) {
                    //msg("Bluetooth Enabled successfully");

                    Set<BluetoothDevice> bt = mBTAdapter.getBondedDevices();

                    if(bt.size()>0) {
                        for (BluetoothDevice device : bt) {
                            if (device.getName().equals("ESP32_LED_Control--2")) {
                                //Toast.makeText(getApplicationContext(), "Ya", Toast.LENGTH_LONG).show();
                                temp = 1;
                                mDevice =  device ;
                                mMaxChars =  mBufferSize;

                                Log.d(TAG, "Ready");


                                if (mBTSocket == null || !mIsBluetoothConnected) {
                                    new ConnectBT().execute();

                                }

                                break;
                            }
                        }

                        if(temp==0) {
                            //Toast.makeText(getApplicationContext(), "No Device Found , Please Pair Device in Bluetooth", Toast.LENGTH_LONG).show();
                            showErrorDialog(R.string.pair);
                        }
                        temp = 0;
                    }

                } else {
                    //msg("Bluetooth couldn't be enabled");
                    showErrorDialog(R.string.bluetoothon);
                }

                break;
            case SETTINGS: //If the settings have been updated
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String uuid = prefs.getString("prefUuid", "Null");
                mDeviceUUID = UUID.fromString(uuid);
                Log.d(TAG, "UUID: " + uuid);
                String bufSize = prefs.getString("prefTextBuffer", "Null");
                mBufferSize = Integer.parseInt(bufSize);

                String orientation = prefs.getString("prefOrientation", "Null");
                Log.d(TAG, "Orientation: " + orientation);
                if (orientation.equals("Landscape")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (orientation.equals("Portrait")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (orientation.equals("Auto")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void msg(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }


    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */



                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Start");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {

        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d(TAG, "Destroy");

        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            progressDialog.setTitle("Hold on");
            progressDialog.setMessage("Connecting...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            //progressDialog = ProgressDialog.show(MainActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                //Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                showErrorDialog(R.string.hardware);
                //finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader

                startCountDownTimer();
            }
            progressDialog.dismiss();
        }

    }


    /**
     * method to initialize the views
     */
    private void initViews() {
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        editTextMinute = (TextView) findViewById(R.id.editTextMinute);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        imageViewStartStop = (Button) findViewById(R.id.imageViewStartStop);

        progressBarCircle2 = (ProgressBar) findViewById(R.id.progressBarCircle2);
        editTextMinute2 = (TextView) findViewById(R.id.editTextMinute2);
        textViewTime2 = (TextView) findViewById(R.id.textViewTime2);
    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {

    }

    /**
     * method to start and stop count down timer
     */
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            // showing the reset icon
            imageViewReset.setVisibility(View.VISIBLE);
            // changing play icon to stop icon
            //imageViewStartStop.setImageResource(R.drawable.icon_stop);
            // making edit text not editable
            editTextMinute.setEnabled(false);
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {

            // hiding the reset icon
            imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            //imageViewStartStop.setImageResource(R.drawable.icon_start);
            // making edit text editable
            editTextMinute.setEnabled(true);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
        }
    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
        int time = 0;
        int time2 = 0;

        if (( ( h1 * 60 * 60 * 1000 ) + ( m1 * 60 * 1000) + ( s1 * 1000) ) >=10000 &&
        ( ( h2 * 60 * 60 * 1000 ) + ( m2 * 60 * 1000) + ( s2 * 1000) ) >=10000 ) {
            // fetching value from edit text and type cast to integer
            time = ( h1 * 60 * 60 * 1000 ) + ( m1 * 60 * 1000) + ( s1 * 1000 ) ;

            time2 = ( h2 * 60 * 60 * 1000 ) + ( m2 * 60 * 1000) + ( s2 * 1000 ) ;

            editTextMinute.setEnabled(false);
            editTextMinute2.setEnabled(false);
            textViewTime.setText(hmsTimeFormatter(time));
            textViewTime2.setText(hmsTimeFormatter(time2));

        } else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time ;
        timeCountInMilliSeconds2 = time2 ;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        imageViewStartStop.setEnabled(false);

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished/ 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(0));
                // call to initialize the progress bar values
                setProgressBarValues2();
                // hiding the reset icon
                //imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                //imageViewStartStop.setImageResource(R.drawable.icon_start);
                // making edit text editable
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;

                try {

                    mBTSocket.getOutputStream().write(on.getBytes());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                new CountDownTimer(1000 , 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        //counttime.setText(String.valueOf(counter));

                    }

                    @Override
                    public void onFinish() {
                        //counttime.setText("LED-1 ON & LED-2 OFF");

                        try {
                            mBTSocket.getOutputStream().write(off.getBytes());
                            startCountDownTimer2();

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    }
                }.start();

            }

        }.start();
        //countDownTimer.start();
    }


    private void startCountDownTimer2() {

        imageViewStartStop.setEnabled(false);

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds2 , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime2.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle2.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime2.setText(hmsTimeFormatter(0));
                // call to initialize the progress bar values
                setProgressBar4();
                // hiding the reset icon
                //imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                //imageViewStartStop.setImageResource(R.drawable.icon_start);
                // making edit text editable
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;

                try {

                    mBTSocket.getOutputStream().write(on2.getBytes());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                new CountDownTimer(1000 , 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        //counttime.setText(String.valueOf(counter));

                    }

                    @Override
                    public void onFinish() {
                        //counttime.setText("LED-1 ON & LED-2 OFF");

                        try {
                            mBTSocket.getOutputStream().write(off2.getBytes());

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        imageViewStartStop.setEnabled(true);
                        editTextMinute.setEnabled(true);
                        editTextMinute2.setEnabled(true);

                    }
                }.start();

            }

        }.start();
        //countDownTimer.start();
    }


    /**
     * method to reset count down timer
     */
    private void reset() {
        //stopCountDownTimer();
        //startCountDownTimer();
    }

    /**
     * method to start count down timer
     */

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    private void setProgressBarValues2() {

        progressBarCircle.setMax((int) 0 / 1000);
        progressBarCircle.setProgress((int) 0 / 1000);
    }

    private void setProgressBar3() {

        progressBarCircle2.setMax((int) timeCountInMilliSeconds2 / 1000);
        progressBarCircle2.setProgress((int) timeCountInMilliSeconds2 / 1000);
    }

    private void setProgressBar4() {

        progressBarCircle2.setMax((int) 0 / 1000);
        progressBarCircle2.setProgress((int) 0 / 1000);
    }

    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;

    }

    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");


        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (MainActivity.this, R.style.AlertDialogThem);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.layout_warning_dailog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Warning...");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText(" Are you sure you want to exit? ");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("No");
        ((ImageView) view.findViewById(R.id.imageIcon))
                .setImageResource(R.drawable.warning);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Intent setIntent = new Intent(Intent.ACTION_MAIN);
                setIntent.addCategory(Intent.CATEGORY_HOME);
                setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(setIntent);

                finish();
            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();

    }

    private void showErrorDialog(int a){
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                            (MainActivity.this, R.style.AlertDialogThem);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.layout_error_dialog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText(getResources().getString(R.string.error_title));
        ((TextView) view.findViewById(R.id.textMessage))
                .setText(a);
        ((Button) view.findViewById(R.id.buttonAction))
                .setText(getResources().getString(R.string.okay));
        ((ImageView) view.findViewById(R.id.imageIcon))
                .setImageResource(R.drawable.error);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
              //  Toast.makeText(MainActivity.this,"Error", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();

    }

}