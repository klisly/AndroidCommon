/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.bloomfilter.iface;

public interface Filter {

	boolean contains(String str);

	void add(String str);

	void remove(String str);

	boolean containsAndAdd(String str);

	long myHashCode(String str);

}
