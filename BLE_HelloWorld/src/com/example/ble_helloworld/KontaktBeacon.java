package com.example.ble_helloworld;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class KontaktBeacon {
	private byte[] advertisingPacket;
	private int Major;
	private int Minor;
	
	public KontaktBeacon(byte[] scan) {
		advertisingPacket = scan;
		
		for (byte entry : scan) {
			System.out.println(Integer.toHexString(entry));
		}

		//Parse Major Number
		ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(scan, 38, 40)); // big-endian by default
		short num = wrapped.getShort();
		Major = num;
		//Parse Minor Number
		wrapped = ByteBuffer.wrap(Arrays.copyOfRange(scan, 40, 42));
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
