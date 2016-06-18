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
    public boolean ur(View view) {
        try {
            mBluetoothServer.send("o".getBytes());
            return true;
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnTouch(R.id.left)
    public boolean left(View view) {
        try {
            mBluetoothServer.send("j".getBytes());
            return true;
        } catch (BluetoothServer.BluetoothServerException | IOException e) {
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
            e.printStackTrace();
        }
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