package kuka.kuka_bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity{


    AtomicBoolean actionDownFlag = new AtomicBoolean(true);

    Handler handler;
    List<Thread> tasks = new ArrayList<>();

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d("Handlers", "Called on main thread");
            for (Thread t : tasks){

                if (t.getState() == Thread.State.NEW)
                {
                    t.start();
                }

            }
            tasks.clear();

            handler.postDelayed(runnableCode, 2000);
        }
    };

    @BindView(R.id.startButton) Button mStartButton;
    @BindView(R.id.stopButton) Button mStopButton;
    @BindView(R.id.sendButton) Button mSendButton;

    @BindView(R.id.ul) Button ul;

    @OnTouch(R.id.up)
    public boolean up(View view) {
        try {
            mBluetoothServer.send("i".getBytes());
            return true;
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnTouch(R.id.ur)
    public boolean ur(View view) {
        try {
            mBluetoothServer.send("o".getBytes());
            return false;
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnTouch(R.id.left)
    public boolean left(View view) {
        try {
            mBluetoothServer.send("j".getBytes());
            mBluetoothServer.wait(1000);
            return true;
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnTouch(R.id.right)
    public boolean right(View view) {
        try {
            mBluetoothServer.send("l".getBytes());
            return true;
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();}
        return false;
    }

    @OnTouch(R.id.dl)
    public boolean dl(View view) {
        try {
            mBluetoothServer.send("m".getBytes());
            return true;
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnTouch(R.id.down)
    public boolean down(View view) {
        try {
            mBluetoothServer.send(",".getBytes());
            return true;
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnTouch(R.id.dr)
    public boolean dr(View view) {
        try {
            mBluetoothServer.send(".".getBytes());
            return true;
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private BluetoothServer mBluetoothServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBluetoothServer = new BluetoothServer();
        mBluetoothServer.setListener(mBluetoothServerListener);

        ul.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Thread t =  new Thread(new Runnable(){
                    public void run(){
                        while(actionDownFlag.get()){
                            if (tasks.isEmpty()) {
                                tasks.add(new Thread(new Runnable(){
                                    public void run(){
                                        try {
                                            mBluetoothServer.send("u".getBytes());
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

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    actionDownFlag.set(true);
                    t.start();


                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    actionDownFlag.set(false);
                }
                return true;
            }
        });


        handler = new Handler();
// Start the initial runnable task by posting through the handler
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
                    mSendButton.setEnabled(true);
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
                    mSendButton.setEnabled(false);
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


}