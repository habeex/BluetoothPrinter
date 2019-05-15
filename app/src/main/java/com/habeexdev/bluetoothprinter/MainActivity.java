package com.habeexdev.bluetoothprinter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.habeexdev.bluetoothprinter.printer.BluetoothActivity;
import com.habeexdev.bluetoothprinter.printer.BluetoothPrinter;

import static android.content.ContentValues.TAG;
import static com.habeexdev.bluetoothprinter.printer.BluetoothPrinter.D;
import static com.habeexdev.bluetoothprinter.printer.BluetoothPrinter.REQUEST_CONNECT_DEVICE;
import static com.habeexdev.bluetoothprinter.printer.BluetoothPrinter.REQUEST_ENABLE_BT;

public class MainActivity extends AppCompatActivity {

    //TODO 9: Declaration BluetoothPrinter
    //BluetoothPrinter declaration
    public BluetoothPrinter bluetoothPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO 10: Initialize
        //Initialize BluetoothPrinter
        bluetoothPrinter = new BluetoothPrinter(MainActivity.this);

        final EditText textToPrint = findViewById(R.id.text_to_print);
        Button printBtn = findViewById(R.id.print_btn);

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = "\n\n\n\n\n\n" +  textToPrint.getText().toString().trim() + "\n\n\n\n\n\n";
                if (content.isEmpty()){
                    textToPrint.setError("Please enter content");
                    textToPrint.requestFocus();
                    return;
                }

                //TODO 13: Check connection and print content
                //check if device is already connected to a Bluetooth Printer device
                if (bluetoothPrinter.isBluetoothConnected()){

                    //Print if connected
                    bluetoothPrinter.printContent(content);

                }else {
                    //start connection request if not connected
                    bluetoothPrinter.startBluetooth();
                }
            }
        });

    }

    //TODO 11: add onActivityResult
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode + " requestCode " + requestCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: {
                bluetoothPrinter.connectPrinterStatus(false);
                // When DeviceListActivity returns with a device to connectPrinter
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(BluetoothActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    try {
                        //send device address (UID)
                        bluetoothPrinter.connectPrinter(address);


                    } catch (Exception e) {
                        // TODO 14: handle exception
                        Log.e(TAG, "Bluetooth Exception");
                    }

                } else {
                    bluetoothPrinter.disConnecteBluetooth();
                    bluetoothPrinter.showSnackBar("Error Connecting to Device");

                }
            }
            break;

            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    bluetoothPrinter.showSnackBar("Bluetooth Enabled.");
                    if(!bluetoothPrinter.isPrinterConnected()) {
                        bluetoothPrinter.connectPrinterStatus(true);
                        bluetoothPrinter.bluetoothConnect();
                    }
                } else {
                    // User did not enable Bluetooth or an error occured
                    bluetoothPrinter.showSnackBar("Bluetooth not connected");
                    bluetoothPrinter.bluetoothEnable();
                }
            }
            break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //TODO 12: Disconnect bluetooth device
        //disconnect bluetoothPrinter
        if (bluetoothPrinter.isBluetoothConnected())
            bluetoothPrinter.mPrinterService.stop();
    }
}
