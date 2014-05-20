package com.example.ble_helloworld;

import android.graphics.Color;


public class KontaktBeacon implements Comparable<KontaktBeacon> {
	private byte[] advertisingPacket;
	private int major;
	private int minor;
	private int txPower;
	private double distance;
	private String name;
	
	public KontaktBeacon(byte[] scan, double rssi) {
		advertisingPacket = scan;

		
		

		//Parse Major Number
		String major = Integer.toHexString(scan[25]) + Integer.toHexString(scan[26]);
		major = major.replaceAll("ffffff", "");
		this.major = Integer.decode("0x"+major);
		//Parse Minor Number
		String minor = Integer.toHexString(scan[27]) + Integer.toHexString(scan[28]);
		minor = minor.replaceAll("ffffff", "");
		this.minor = Integer.decode("0x"+minor);
		
		setName(this.major);
		
		this.txPower = -77;
		
		distance = this.calculateDistance(txPower, rssi);
		System.out.println("Distance: " + distance);
	}
	
	public KontaktBeacon(int major, int minor) {
		this.major = major;
		this.minor = minor;
		setName(this.major);
		this.txPower=-77;
	}
	
	public byte[] getAdvertisingPacket() {
		return advertisingPacket;
	}
	
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	
	public int getTxPower() {
		return txPower;
	}

	public void setTxPower(int txPower) {
		this.txPower = txPower;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(int major) {
		if (major == 50147) {
    		this.name = "8ynP";
    	}
    	if (major == 54172) {
    		this.name = "W57g";
    		}
    	if (major == 13115) {
    		this.name = "oGLc";
    		}
    	if (major == 62947) {
    		this.name = "yNOV";
    		}
    	if (major == 53963) {
    		this.name = "U66c";
    		}
	}

	//Distance returned in meters
	private double calculateDistance(int txPower, double rssi) {
		  if (rssi == 0) {
		    return -1.0; // if we cannot determine accuracy, return -1.
		  }

		  double ratio = rssi*1.0/txPower;
		  if (ratio < 1.0) {
			System.out.println("RSSI :" + rssi);
			System.out.println("Major :" + major);
			return Math.pow(ratio,10);
		  }
		  else {
		    double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;    
			System.out.println("RSSI :" + rssi);
			System.out.println("Major :" + major);
			return accuracy;
		  }
		  
	}

	
	@Override
	public boolean equals(Object object) {
		if(object instanceof KontaktBeacon) {
			KontaktBeacon other = (KontaktBeacon)object;
			if(this.major == other.major && this.minor == other.minor) {
				return true;
			}
		} else {
			return false;
		}
		
		return false;
		
	}

	@Override
	public int compareTo(KontaktBeacon other) {
		return (int) (this.getDistance()-other.getDistance());
	}
	
}
