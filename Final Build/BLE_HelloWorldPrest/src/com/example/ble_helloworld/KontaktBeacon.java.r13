package com.example.ble_helloworld;


public class KontaktBeacon implements Comparable<KontaktBeacon> {
	private byte[] advertisingPacket;
	private int major;
	private int minor;
	private int txPower;
	private double distance;
	
	public KontaktBeacon(byte[] scan, double rssi) {
		advertisingPacket = scan;

		int count = 1;
		for (byte entry : scan) {
			System.out.println("Byte: " + count);
			System.out.println(Integer.toHexString(entry));
		}
		

		//Parse Major Number
		String major = Integer.toHexString(scan[25]) + Integer.toHexString(scan[26]);
		major = major.replaceAll("ffffff", "");
		this.major = Integer.decode("0x"+major);
		//Parse Minor Number
		String minor = Integer.toHexString(scan[27]) + Integer.toHexString(scan[28]);
		minor = minor.replaceAll("ffffff", "");
		this.minor = Integer.decode("0x"+minor);
		
		
		this.txPower = -77;
		
		distance = this.calculateDistance(txPower, rssi);
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

	//Distance returned in meters
	private double calculateDistance(int txPower, double rssi) {
		  if (rssi == 0) {
		    return -1.0; // if we cannot determine accuracy, return -1.
		  }

		  double ratio = rssi*1.0/txPower;
		  if (ratio < 1.0) {
		    return Math.pow(ratio,10);
		  }
		  else {
		    double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;    
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
