package kuka.kuka_bluetooth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.output) EditText mOutputView;

    @BindView(R.id.startButton) Button mStartButton;
    @BindView(R.id.stopButton) Button mStopButton;
    @BindView(R.id.sendButton) Button mSendButton;

    @OnTouch(R.id.ul)
    public void ul(View view) {
        try {
            mBluetoothServer.send("u".getBytes());
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnTouch(R.id.up)
    public void up(View view) {
        try {
            mBluetoothServer.send("i".getBytes());
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnTouch(R.id.ur)
    public void ur(View view) {
        try {
            mBluetoothServer.send("o".getBytes());
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnTouch(R.id.left)
    public void left(View view) {
        try {
            mBluetoothServer.send("j".getBytes());
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnTouch(R.id.right)
    public void right(View view) {
        try {
            mBluetoothServer.send("l".getBytes());
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnTouch(R.id.dl)
    public void dl(View view) {
        try {
            mBluetoothServer.send("m".getBytes());
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnTouch(R.id.down)
    public void down(View view) {
        try {
            mBluetoothServer.send(",".getBytes());
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnTouch(R.id.dr)
    public void dr(View view) {
        try {
            mBluetoothServer.send(".".getBytes());
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
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

    public void onSendClick(View view){
        try {
            mBluetoothServer.send(mOutputView.getText().toString().getBytes());
            mOutputView.setText("");
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
            Log.i("Error",e.getMessage().toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Error",e.getMessage().toString());
        }
    }

}