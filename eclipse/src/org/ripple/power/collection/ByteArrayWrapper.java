package org.ripple.power.collection;

import java.util.Arrays;

import org.ripple.bouncycastle.util.encoders.Hex;


public class ByteArrayWrapper implements Comparable<ByteArrayWrapper> {

	private final byte[] data;

	public ByteArrayWrapper(byte[] data) {
		if (data == null)
			throw new NullPointerException("Data must not be null");
		this.data = data;
	}

	public boolean equals(Object other) {
		if (!(other instanceof ByteArrayWrapper)) {
			return false;
		}
		byte[] otherData = ((ByteArrayWrapper) other).getData();
		return FastBytes.compareTo(data, 0, data.length, otherData, 0,
				otherData.length) == 0;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}

	@Override
	public int compareTo(ByteArrayWrapper o) {
		return FastBytes.compareTo(data, 0, data.length, o.getData(), 0,
				o.getData().length);
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public String toString() {
		return Hex.toHexString(data);
	}
}
