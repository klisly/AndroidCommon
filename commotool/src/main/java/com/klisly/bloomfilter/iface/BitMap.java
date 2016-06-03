/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.bloomfilter.iface;

public interface BitMap {

	int MACHINE32 = 32;

	int MACHINE64 = 32;

	void add(long i);

	boolean contains(long i);

	void remove(long i);
}
