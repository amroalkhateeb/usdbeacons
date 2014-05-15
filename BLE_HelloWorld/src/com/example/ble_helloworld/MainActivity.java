package com.example.ble_helloworld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	BluetoothManager bluetoothManager;
	private List<KontaktBeacon> beacons = new ArrayList<KontaktBeacon>();
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private BluetoothDevice bdevice;
    private BluetoothGatt gattServer = null;
    private ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    Scan scan = new Scan();
    private TextView output;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		output = (TextView) findViewById(R.id.output);
		output.setText("");
		Scan scan = new Scan();
		// Initializes Bluetooth adapter.
		bluetoothManager  = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

		
		mBluetoothAdapter = bluetoothManager.getAdapter();
		int REQUEST_ENABLE_BT = 1;
		// Ensures Bluetooth is available on the device and it is enabled. If not,
		// displays a dialog requesting user permission to enable Bluetooth.
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
	        new BluetoothAdapter.LeScanCallback() {
	    @Override
	    public void onLeScan(final BluetoothDevice device, int rssi,
	            byte[] scanRecord) {
	    	System.out.println("Scan Record Length: " + scanRecord.length);
	    	KontaktBeacon beacon = new KontaktBeacon(scanRecord);
	    	if (!beacons.contains(beacon)) {
	    		System.out.println("Adding new beacon");
		    	beacons.add(beacon);
	    	}
	        runOnUiThread(new Runnable() {
	           @Override
	           public void run() {
	       			TextView output = (TextView)findViewById(R.id.output);
	       			output.setText("I can see that iPhone you have there. Very sexy.");
	       			bdevice = device;
	       			if (!devices.contains(bdevice)) {
	       				devices.add(bdevice);
	       			}
	       			
	           }
	       });
	   }
	};

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        
    }
    
    public void scan(View view) {
    	scanLeDevice(true);    	
    }
    
    BluetoothGatt mBluetoothGatt;
    
    public void connect(View view) {
    	if (bdevice != null) {
    		 runOnUiThread(new Runnable() {
  	           @Override
  	           public void run() {
  	        	   System.out.println("Number of devices found: " + devices.size());
  	        	   for (BluetoothDevice device : devices) {
  	        		  while (gattServer == null) {
  	        	   try {
  	        		   System.out.println("Connecting to device: " + device.getAddress());
  	        		   	gattServer = device.connectGatt(getApplicationContext(), false, scan.getCallback());
  	        		   	/*services = gattServer.getServices();
  	        		   	if(services.size() > 0) {
  	        		   		characteristics = services.get(0).getCharacteristics();
  	        		   		System.out.println(gattServer.readDescriptor(characteristics.get(0).getDescriptors().get(0)));
  	        		   	}*/
				} catch (Exception e) {
					e.printStackTrace();
					}
  	        		}
  	        	 }
  	           }
  	       });
    		BluetoothProfile bProfile;
    		System.out.println(bluetoothManager.getAdapter().getAddress());
    		//mBluetoothGatt.connect();
    	} else {
    		System.out.println("Failure!");
    	}
    }
    
    public void display(View view) throws IOException, InterruptedException, ExecutionException {
    	 System.out.println("Number of Beacons found: " + beacons.size());
    	 for (KontaktBeacon beacon : beacons) {
        	 System.out.println("Beacon Major Number: " + beacon.getMajor());
        	 System.out.println("Beacon Minor Number: " + beacon.getMinor());
    	 }
    }
    
    class GetRequest extends AsyncTask<String, Void, String> {


    	protected String doInBackground(String... upc) {
    		
    		String json = "";
            try {
            	json = Request.sendGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (json.length() > 0) { 
            	return json; 
            } else {
            	return null;
            }
        }	
    }
    
}
