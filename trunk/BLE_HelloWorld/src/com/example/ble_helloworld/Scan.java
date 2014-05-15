package com.example.ble_helloworld;

import java.util.UUID;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class Scan extends Service {
    private final static String TAG = Scan.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.ble_helloworld.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.ble_helloworld.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.ble_helloworld.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.ble_helloworld.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.ble_helloworld.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString("8492e75f-4fd6-469d-b132-043fe94921d8");

    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                int newState) {
            String intentAction;
            System.out.println("CONNECTION STATE HAS CHANGED");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery");
                Log.i(TAG, "Service discovery start = " + gatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }
        

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        	System.out.println("SCANNING SERVICES");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Successful Discovery:" + gatt.getServices().size());
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
        
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        		System.out.println(descriptor.getPermissions());
        }
    };

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void broadcastUpdate(final String action) {
	    final Intent intent = new Intent();
	    //sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
	                             final BluetoothGattCharacteristic characteristic) {
	    final Intent intent = new Intent(action);

	    // This is special handling for the Heart Rate Measurement profile. Data
	    // parsing is carried out as per profile specifications.
	    if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
	        int flag = characteristic.getProperties();
	        int format = -1;
	        if ((flag & 0x01) != 0) {
	            format = BluetoothGattCharacteristic.FORMAT_UINT16;
	            Log.d(TAG, "Heart rate format UINT16.");
	        } else {
	            format = BluetoothGattCharacteristic.FORMAT_UINT8;
	            Log.d(TAG, "Heart rate format UINT8.");
	        }
	        final int heartRate = characteristic.getIntValue(format, 1);
	        Log.d(TAG, String.format("Received heart rate: %d", heartRate));
	        intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
	    } else {
	        // For all other profiles, writes the data formatted in HEX.
	        final byte[] data = characteristic.getValue();
	        if (data != null && data.length > 0) {
	            final StringBuilder stringBuilder = new StringBuilder(data.length);
	            for(byte byteChar : data)
	                stringBuilder.append(String.format("%02X ", byteChar));
	            intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
	                    stringBuilder.toString());
	        }
	    }
	    sendBroadcast(intent);
	}

	public BluetoothGattCallback getCallback() {
    	return mGattCallback;
	}
	
}
