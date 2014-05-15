package com.example.ble_helloworld;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class KontaktBeacon {
	private byte[] advertisingPacket;
	private int Major;
	private int Minor;
	
	public KontaktBeacon(byte[] scan) {
		advertisingPacket = scan;
		//Parse Major Number
		ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(scan, 25, 27)); // big-endian by default
		short num = wrapped.getShort();
		Major = num;
		//Parse Minor Number
		wrapped = ByteBuffer.wrap(Arrays.copyOfRange(scan, 27, 29));
		num = wrapped.getShort();
		Minor = num;
	}
	
	public byte[] getAdvertisingPacket() {
		return advertisingPacket;
	}
	
	public int getMajor() {
		return Major;
	}
	public void setMajor(int major) {
		Major = major;
	}
	public int getMinor() {
		return Minor;
	}
	public void setMinor(int minor) {
		Minor = minor;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof KontaktBeacon) {
			KontaktBeacon other = (KontaktBeacon)object;
			if(this.advertisingPacket == other.advertisingPacket) {
				return true;
			}
		} else {
			return false;
		}
		
		return false;
		
	}
	
}
