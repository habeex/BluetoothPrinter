package com.habeexdev.bluetoothprinter.printer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import static android.support.constraint.Constraints.TAG;

// TODO 5: create BluetoothPrinter
public class BluetoothPrinter {

    //bluetooth
    public static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    public BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    public PrinterService mPrinterService = null;

    public BluetoothDevice mBluetoothDevice=null;

    private boolean isConnecting = false;
    public boolean isBluetoothConnected = false;
    public ProgressDialog progress;
    public String line = "    *************************";

    public Context context;

    public void printContent(String contentToPrint){
       mPrinterService.write(contentToPrint);

    }


    public boolean isBluetoothConnected(){
        return isBluetoothConnected;
    }

    public void disConnecteBluetooth(){
        isBluetoothConnected = false;
    }


    public void connectPrinter(String address){
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        mPrinterService.connect(mBluetoothDevice);
    }

    public void connectPrinterStatus(boolean isConnecting){
        this.isConnecting = isConnecting;
    }

    public boolean isPrinterConnected(){
        return isConnecting;
    }

    public void startBluetooth(){
        try {
            bluetoothInit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothPrinter(Context context) {
        this.context = context;
        progress = new ProgressDialog((Activity)context);
    }

    public BluetoothPrinter() {
    }

    public void bluetoothInit() throws IOException {

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Initialize the BluetoothService to perform bluetooth connections
        mPrinterService = PrinterService.getInstance(context, mHandler);

        Log.d("test", "first");
        if (mBluetoothAdapter != null) {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            if (!mBluetoothAdapter.isEnabled()) {
                showSnackBar("Bluetooth is Disabled");
                Log.d("test", "second");
                bluetoothEnable();
            } else {
                //if (mBluetoothService == null) blueToothSetup();
                if(!isConnecting) {
                    isConnecting = true;
                    bluetoothConnect();
                }

            }
        }else {
            showSnackBar("Bluetooth is not Available");
        }
    }

    public void bluetoothEnable() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity)context).startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    public void bluetoothConnect() {
        progress.setMessage("Connecting to Device...");
        // Launch the BluetoothActivity to see devices and do scan
        Intent serverIntent = new Intent((Activity)context, BluetoothActivity.class);
        ((Activity)context).startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    // The Handler that gets information back from the BluetoothService
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case PrinterService.STATE_CONNECTED: {

                            isBluetoothConnected = true;
                            isConnecting = false;
                            if (progress != null && progress.isShowing()) {
                                progress.dismiss();
                            }
                        }
                        break;

                        case PrinterService.STATE_CONNECTING: {
                            isConnecting = true;
                            showSnackBar("Please wait connecting...");
                            if(progress != null && !progress.isShowing()){
                                progress.show();
                            }
                        }
                        break;
                        case PrinterService.STATE_LISTEN:
                        case PrinterService.STATE_NONE: {
                            if (progress != null && progress.isShowing()) {
                                progress.dismiss();
                            }

                            isConnecting = false;
                            isBluetoothConnected = false;
                            showSnackBar("Error Connecting to Device");
                        }
                        break;
                    }
                    break;
            }
        }
    };


    public void showSnackBar(String msg) {
        View root_layout =  ((Activity)context).findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(root_layout, msg, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

}
