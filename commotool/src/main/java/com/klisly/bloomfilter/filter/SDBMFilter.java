/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.bloomfilter.filter;

import com.klisly.bloomfilter.iface.BitMap;
import com.klisly.bloomfilter.iface.Filter;
import com.klisly.bloomfilter.iface.IntegerMap;
import com.klisly.bloomfilter.iface.LongMap;

public class SDBMFilter implements Filter {

	private BitMap bm = null;

	private long size = 0;

	public SDBMFilter(long maxValue, int MACHINENUM) throws Exception {
		this.size = maxValue;
		if (MACHINENUM == 32) {
			bm = new IntegerMap((int) (size / MACHINENUM));
		} else if (MACHINENUM == 64) {
			bm = new LongMap((int) (size / MACHINENUM));
		} else {
			throw new Exception("����Ļ���λ������");
		}
	}

	public SDBMFilter(long maxValue) {
		this.size = maxValue;
		bm = new IntegerMap((int) (size / 32));
	}

	@Override
	public boolean contains(String str) {
		// TODO Auto-generated method stub
		long hash = this.myHashCode(str);
		return bm.contains(hash);
	}

	@Override
	public void add(String str) {
		// TODO Auto-generated method stub
		long hash = this.myHashCode(str);
		bm.add(hash);
	}

	@Override
	public boolean containsAndAdd(String str) {
		// TODO Auto-generated method stub
		long hash = this.myHashCode(str);
		if (bm.contains(hash)) {
			return true;
		} else {
			bm.add(hash);
		}
		return false;
	}

	@Override
	public long myHashCode(String str) {
		// TODO Auto-generated method stub
		int hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = str.charAt(i) + (hash << 6) + (hash << 16) - hash;
		}

		if (hash < 0)
			hash *= -1;

		return hash % size;
	}

	@Override
	public void remove(String str) {
		long hash = this.myHashCode(str);
		bm.remove(hash);
	}

}
