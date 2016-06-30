package kuka.kuka_bluetooth;

/**
 * Created by Bjoern Einert on 28.06.2016.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TactileFeedbackPlayer {

    public boolean udp_connected = false;
    public boolean ready = true;
    public DatagramSocket clientSocket;
    public int udpPort = 50000;
    public String udpIP = "127.0.0.1";
    public DatagramPacket sendPacket;
    public DatagramPacket receivePacket;
    public String sendString;




    public void vibrationTest(View view){
        String answer = sendUDP("!PlayPattern,test_one_by_one", 4000);
        Log.i("Info",answer);
    }

    public boolean connect(){
        if(!udp_connected){
            String test = new String (udpConnect(this.udpIP, udpPort));
            if (test.toLowerCase().trim().equals("null")){
                udp_connected = false;
                Log.i("Info","not connected");
                return false;
            }else if (test.toLowerCase().trim().equals("timeout")){
                Log.i("Info","not connected");
                return false;
            }else{
                // Connected through the UPD port.
                // Display info for multiple items
                // Enable and update buttons
                udp_connected = true;
                Log.i("Info","connected");

                return true;
            }
        } else {
            udp_connected = false;
            Log.i("Info","not connected");
            closeUdpConnection();
            return false;
        }
    }

    private String udpConnect(String ip, int port){
        sendString = "?getServerVersion";

        new ConnectionThread().execute();
        ready=false;
        while(!ready){

        }
        String modifiedSentence = new String(receivePacket.getData());
        Log.i("Info",modifiedSentence);
        modifiedSentence = modifiedSentence.substring(0, receivePacket.getLength());
        modifiedSentence = modifiedSentence.split(",")[1];

        udpIP = ip;
        clientSocket.close();
        return modifiedSentence;
    }


    public String sendUDP(String command, int delay){
        if(!ready)return "";
        sendString = command;

        new ConnectionThread().execute();
        ready=false;
        while(!ready){

        }
        String answer = new String(receivePacket.getData());
        Log.i("Info",answer);
        answer = answer.substring(0, receivePacket.getLength());

        clientSocket.close();
        new HinderThread(delay).execute();
        return answer.substring(answer.indexOf(",") + 1, answer.length());
    }

    private void closeUdpConnection(){
        try {
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("No open connection");
        }
    }

    private class ConnectionThread extends AsyncTask<Object, Object, Boolean> {

        @Override
        protected Boolean doInBackground(Object... objects) {
            try {
                try {
                    clientSocket = new DatagramSocket();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                try {
                    clientSocket.setSoTimeout(1000);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                InetAddress IPAddress = null;
                try {
                    IPAddress = InetAddress.getByName(udpIP);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                byte[] receiveData = new byte[2048];
                byte[] sendData = sendString.trim().getBytes();

                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, udpPort);

                try {
                    clientSocket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                //	System.out.println("x");

                try {
                    clientSocket.receive(receivePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch(Exception e){
                e.printStackTrace();
            }
            ready=true;
            return true;
        }
    }

    private class HinderThread extends AsyncTask<Object, Object, Boolean> {

        private int delay;

        public HinderThread(int delay){
            this.delay=delay;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ready=false;
            try {
                Thread.sleep(delay);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            ready=true;
            return null;
        }
    }
}


