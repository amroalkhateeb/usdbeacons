package com.example.ble_helloworld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.example.ble_helloworld.KontaktBeacon;
import com.example.ble_helloworld.R;
import com.example.ble_helloworld.Request;
import com.example.ble_helloworld.Scan;

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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	BluetoothManager bluetoothManager;
	private List<KontaktBeacon> beacons = new ArrayList<KontaktBeacon>();
	private BluetoothAdapter mBluetoothAdapter;
    RelativeLayout layout;
    private BluetoothDevice bdevice;
    private BluetoothGatt gattServer = null;
    private ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    Scan scan = new Scan();
    private TextView output;
	private TextView output2;
	KontaktBeacon U66c = new KontaktBeacon(53963,44832);
	KontaktBeacon ynP = new KontaktBeacon(50147,18796);
	KontaktBeacon yNOV = new KontaktBeacon(62947,556);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Scan scan = new Scan();
		bluetoothManager  = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

		
		mBluetoothAdapter = bluetoothManager.getAdapter();
		int REQUEST_ENABLE_BT = 1;
		// Ensures Bluetooth is available on the device and it is enabled. If not,
		// displays a dialog requesting user permission to enable Bluetooth.
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		layout = (RelativeLayout) findViewById(R.id.layout);
		new BluetoothScanTask().execute(); 
		output = (TextView)findViewById(R.id.stack);
		output2 = (TextView)findViewById(R.id.time);
		
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
	    	KontaktBeacon beacon = new KontaktBeacon(Arrays.copyOfRange(scanRecord, 0, 53),rssi);
	    	//if (!beacons.contains(beacon)) {
	    		System.out.println("Adding new beacon");
		    	beacons.add(beacon);
			

	    	//}
	        runOnUiThread(new Runnable() {
	           @Override
	           public void run() {
	       			bdevice = device;
	       			if (!devices.contains(bdevice)) {
	       				devices.add(bdevice);
	       			}	       	
	           }
	       });
	   }
	};

    
    
    public void scan(View view) {

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
    
    
    class BluetoothScanTask extends AsyncTask<Void, Void, Void> {

    	private boolean mScanning = false;
        private Handler mHandler = new Handler();

    	protected Void doInBackground(Void... param) {
    		while(true) {
    			if (!mScanning) {
    				scanLeDevice(true);
    			} else {
    					runOnUiThread(new Runnable() {
    					     @Override
    					     public void run() {
    			    				if (beacons.size() >= 15) {
    			    					Collections.sort(beacons);
    			    					System.out.println(beacons.size());
    			    					System.out.println("Closest Beacon Distance: " + beacons.get(0).getDistance());
    			    					layout.setBackgroundColor(getColorFromMajor(beacons.get(0).getMajor()));
    			    					String beaconList = "";
    			    					if (beacons.contains(U66c) && beacons.contains(ynP) && beacons.contains(yNOV)) {
    			    					beaconList = "U66c " + beacons.get(beacons.indexOf(U66c)).getDistance() + "\n" +
    			    								"8ynP " + beacons.get(beacons.indexOf(ynP)).getDistance() + "\n" +
    			    								"yNOV " + beacons.get(beacons.indexOf(yNOV)).getDistance() + "\n";	
    			    						
    			    					}
    			    					output.setText(beaconList);
    			        				beacons = new ArrayList<KontaktBeacon>();

    			    				}
    			    				
    					    }
    					});
    			}
    		}
        }
    	
    	// Stops scanning after 10 seconds.
        private static final long SCAN_PERIOD = 1000;

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
        
        private int getColorFromMajor(int major) {
        	if (major == 50147) {
        		return Color.GREEN;
        	}
        	if (major == 54172) {
        		return Color.YELLOW;
        	}
        	if (major == 13115) {
        		return Color.BLUE;
        	}
        	if (major == 62947) {
        		return Color.parseColor("#FF3300");
        	}
        	if (major == 53963) {
        		return Color.MAGENTA;
        	}
        	return Color.BLACK;
        }
        
    }
    
}
