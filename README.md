# BluetoothPrinter

Android thermal printers via bluetooth connection.

## TODO
Follow 13 steps on this project to integrate your universal Bluetooth printer

1. Create activity_bluetooth.mxl
2. Define bluetooth string text
3. Create bluetooth_device_name.mxl
4. Create PrinterService.class
5. Create BluetoothActivity.class
6. Create BluetoothPrinter.class

7. Declare BLUETOOTH permission in your AndroidManifest.xml
```
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
``` 
8. Add BluetoothActivity to AndroidManifest.xml
```
<activity
      android:name=".printer.BluetoothActivity"
      android:configChanges="orientation|keyboardHidden"
      android:label="@string/select_device"
      android:theme="@android:style/Theme.Holo.Light.Dialog" />
```
9. Declaration BluetoothPrinter in your activity class
```
public BluetoothPrinter bluetoothPrinter;
```
10. Initialize bluetoothPrinter
```
@Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      //Initialize BluetoothPrinter
      bluetoothPrinter = new BluetoothPrinter(MainActivity.this);
      ....
   }
```

11. Add onActivityResult
```
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
```
12. Disconnect bluetooth device
```
 @Override
    protected void onStop() {
        super.onStop();
     if (bluetoothPrinter.isBluetoothConnected()){
                bluetoothPrinter.mPrinterService.stop();
      }
 }
```
13. Check connection and print content
```
  if (bluetoothPrinter.isBluetoothConnected()){

        //Print if connected
        bluetoothPrinter.printContent(content);

    }else {
        //start connection request if not connected
        bluetoothPrinter.startBluetooth();
    }
```
