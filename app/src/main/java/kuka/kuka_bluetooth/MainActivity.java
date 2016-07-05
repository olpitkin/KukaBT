package kuka.kuka_bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity{


    AtomicBoolean actionDownFlag = new AtomicBoolean(true);

    Handler handler;
    List<Thread> tasks = new ArrayList<>();


    @BindView(R.id.layout_joystick) RelativeLayout layout_joystick;
    @BindView(R.id.textView5) TextView textView5;
    JoyStickClass js;
    ImageView image_joystick, image_border;
    private TactileFeedbackPlayer vibrator = new TactileFeedbackPlayer();

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d("Handlers", "Called on main thread");
            Log.d("Handlers", String.valueOf(tasks.size()));
                    if (!tasks.isEmpty()) {
                        tasks.get(0).start();
                        tasks.clear();
                    }
            handler.postDelayed(runnableCode, 1500);
        }
    };

    @BindView(R.id.startButton) Button mStartButton;
    @BindView(R.id.stopButton) Button mStopButton;

    @BindView(R.id.vibrConnection) Button mVibrConnectButton;
    @BindView(R.id.stopBtn) Button mStoppingButton;
    @BindView(R.id.crossBtn) Button mCrossingButton;
    @BindView(R.id.fullStop) Button mFullStop;

    @OnClick(R.id.speedUp)
    public void speedUp(Button button) {
        try {
            mBluetoothServer.send("q".getBytes());
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.speedDown)
    public void speedDown(Button button) {
        try {
            mBluetoothServer.send("z".getBytes());
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private BluetoothServer mBluetoothServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBluetoothServer = new BluetoothServer();
        mBluetoothServer.setListener(mBluetoothServerListener);

        js = new JoyStickClass(getApplicationContext(), layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);

                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {

                    actionDownFlag.set(true);

/*                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));*/

                    int direction = js.get8Direction();
                    if(direction == JoyStickClass.STICK_UP) {
                        textView5.setText("Direction : Up");

                        Thread t =  new Thread(new Runnable(){
                            public void run(){
                                while(actionDownFlag.get()){
                                    if (tasks.isEmpty()) {
                                        tasks.add(new Thread(new Runnable(){
                                            public void run(){
                                                try {
                                                    mBluetoothServer.send("i".getBytes());
                                                } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }));
                                    }
                                    //maybe sleep some times to not polute your logcat
                                }
                                //Log.d(VIEW_LOG_TAG, "Not Touching");
                            }
                        });

                        t.start();

                    } else if(direction == JoyStickClass.STICK_UPRIGHT) {
                        textView5.setText("Direction : Up Right");
                        Thread t =  new Thread(new Runnable(){
                            public void run(){
                                while(actionDownFlag.get()){
                                    if (tasks.isEmpty()) {
                                        tasks.add(new Thread(new Runnable(){
                                            public void run(){
                                                try {
                                                    mBluetoothServer.send("o".getBytes());
                                                    if(vibrator.udp_connected)vibrator.sendUDP("!PlayPattern,move_right", 250);
                                                } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }));
                                    }
                                    //maybe sleep some times to not polute your logcat
                                }
                                //Log.d(VIEW_LOG_TAG, "Not Touching");
                            }
                        });

                        t.start();
                    } else if(direction == JoyStickClass.STICK_RIGHT) {
                        textView5.setText("Direction : Right");
                        Thread t =  new Thread(new Runnable(){
                            public void run(){
                                while(actionDownFlag.get()){
                                    if (tasks.isEmpty()) {
                                        tasks.add(new Thread(new Runnable(){
                                            public void run(){
                                                try {
                                                    mBluetoothServer.send("l".getBytes());
                                                    if(vibrator.udp_connected)vibrator.sendUDP("!PlayPattern,move_right", 250);
                                                } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }));
                                    }
                                    //maybe sleep some times to not polute your logcat
                                }
                                //Log.d(VIEW_LOG_TAG, "Not Touching");
                            }
                        });

                        t.start();

                    } else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
                        textView5.setText("Direction : Down Right");
                        Thread t =  new Thread(new Runnable(){
                            public void run(){
                                while(actionDownFlag.get()){
                                    if (tasks.isEmpty()) {
                                        tasks.add(new Thread(new Runnable(){
                                            public void run(){
                                                try {
                                                    mBluetoothServer.send(".".getBytes());
                                                    if(vibrator.udp_connected)vibrator.sendUDP("!PlayPattern,move_right", 250);
                                                } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }));
                                    }
                                    //maybe sleep some times to not polute your logcat
                                }
                                //Log.d(VIEW_LOG_TAG, "Not Touching");
                            }
                        });

                        t.start();

                    } else if(direction == JoyStickClass.STICK_DOWN) {
                        textView5.setText("Direction : Down");
                        Thread t =  new Thread(new Runnable(){
                            public void run(){
                                while(actionDownFlag.get()){
                                    if (tasks.isEmpty()) {
                                        tasks.add(new Thread(new Runnable(){
                                            public void run(){
                                                try {
                                                    mBluetoothServer.send(",".getBytes());
                                                } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }));
                                    }
                                    //maybe sleep some times to not polute your logcat
                                }
                                //Log.d(VIEW_LOG_TAG, "Not Touching");
                            }
                        });

                        t.start();
                    } else if(direction == JoyStickClass.STICK_DOWNLEFT) {
                        textView5.setText("Direction : Down Left");
                        Thread t =  new Thread(new Runnable(){
                            public void run(){
                                while(actionDownFlag.get()){
                                    if (tasks.isEmpty()) {
                                        tasks.add(new Thread(new Runnable(){
                                            public void run(){
                                                try {
                                                    mBluetoothServer.send("m".getBytes());
                                                    if(vibrator.udp_connected)vibrator.sendUDP("!PlayPattern,move_left", 250);
                                                } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }));
                                    }
                                    //maybe sleep some times to not polute your logcat
                                }
                                //Log.d(VIEW_LOG_TAG, "Not Touching");
                            }
                        });

                        t.start();
                    } else if(direction == JoyStickClass.STICK_LEFT) {
                        textView5.setText("Direction : Left");
                        Thread t =  new Thread(new Runnable(){
                            public void run(){
                                while(actionDownFlag.get()){
                                    if (tasks.isEmpty()) {
                                        tasks.add(new Thread(new Runnable(){
                                            public void run(){
                                                try {
                                                    mBluetoothServer.send("j".getBytes());
                                                    if(vibrator.udp_connected)vibrator.sendUDP("!PlayPattern,move_left", 250);
                                                } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }));
                                    }
                                    //maybe sleep some times to not polute your logcat
                                }
                                //Log.d(VIEW_LOG_TAG, "Not Touching");
                            }
                        });

                        t.start();

                    } else if(direction == JoyStickClass.STICK_UPLEFT) {
                        textView5.setText("Direction : Up Left");
                        Thread t =  new Thread(new Runnable(){
                            public void run(){
                                while(actionDownFlag.get()){
                                    if (tasks.isEmpty()) {
                                        tasks.add(new Thread(new Runnable(){
                                            public void run(){
                                                try {
                                                    mBluetoothServer.send("u".getBytes());
                                                    if(vibrator.udp_connected)vibrator.sendUDP("!PlayPattern,move_left", 250);
                                                } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }));
                                    }
                                    //maybe sleep some times to not polute your logcat
                                }
                                //Log.d(VIEW_LOG_TAG, "Not Touching");
                            }
                        });

                        t.start();

                    } else if(direction == JoyStickClass.STICK_NONE) {
                        textView5.setText("Direction : Center");
                    }
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {

                    actionDownFlag.set(false);
                    textView5.setText("Direction :");
                }
                return true;
            }
        });

        handler = new Handler();
        handler.post(runnableCode);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetoothServer.stop();
        mBluetoothServer = null;
    }

    /**
     * Bluetooth server events listener.
     */
    private BluetoothServer.IBluetoothServerListener mBluetoothServerListener =
            new BluetoothServer.IBluetoothServerListener() {
                @Override
                public void onStarted() {
                    Log.i("Info","Server has started, waiting for client connection");
                    mStopButton.setEnabled(true);
                    mStartButton.setEnabled(false);
                }

                @Override
                public void onConnected() {
                    Log.i("Info","Client connected");
            }

                @Override
                public void onData(byte[] data) {
                    Log.i("Message",new String(data));
                }

                @Override
                public void onError(String message) {
                    Log.i("Error",message);
                }

                @Override
                public void onStopped() {
                    Log.i("Info","Server has stopped");
                    mStopButton.setEnabled(false);
                    mStartButton.setEnabled(true);
                }
            };

    public void onStartClick(View view){
        try {
            mBluetoothServer.start();
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
            Log.i("Error",e.getMessage().toString());
        }
    }

    public void onStopClick(View view){
        mBluetoothServer.stop();
    }

    public void onVibrConClick(View view){
        vibrator.connect();
        if(vibrator.udp_connected){
            mVibrConnectButton.setText("Disconnect Vibration");
        }
        else mVibrConnectButton.setText("Connect Vibration");
    }
    public void onStopping(View view){
        if(vibrator.udp_connected)vibrator.sendUDP("!PlayPattern,stop_ahead", 600);
    }

    public void onCrossing(View view){
        if(vibrator.udp_connected)vibrator.sendUDP("!PlayPattern,crossing_ahead", 800);
    }

    public void onFullStop(View view) {
        if (mFullStop.getText().equals("STOP")) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    while (actionDownFlag.get()) {
                        if (tasks.isEmpty()) {
                            tasks.add(new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        mBluetoothServer.send("fullStop".getBytes());
                                    } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }));
                        }
                        //maybe sleep some times to not polute your logcat
                    }
                    //Log.d(VIEW_LOG_TAG, "Not Touching");
                }
            });
            mFullStop.setText("go on");
        } else {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    while (actionDownFlag.get()) {
                        if (tasks.isEmpty()) {
                            tasks.add(new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        mBluetoothServer.send("goOn".getBytes());
                                    } catch (BluetoothServer.BluetoothServerException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }));
                        }
                        //maybe sleep some times to not polute your logcat
                    }
                    //Log.d(VIEW_LOG_TAG, "Not Touching");
                }
            });
            mFullStop.setText("STOP");
        }

    }
}