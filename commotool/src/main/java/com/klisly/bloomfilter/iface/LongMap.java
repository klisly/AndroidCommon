/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.bloomfilter.iface;

public class LongMap implements BitMap {

    private static final long MAX = Long.MAX_VALUE;
    private long[] longs = null;

    public LongMap() {
        longs = new long[93750000];
    }

    public LongMap(int size) {
        longs = new long[size];
    }

    public void add(long i) {
        int r = (int) (i / 64);
        int c = (int) (i % 64);
        longs[r] = (int) (longs[r] | (1 << c));
    }

    public boolean contains(long i) {
        int r = (int) (i / 64);
        int c = (int) (i % 64);
        return ((int) ((longs[r] >>> c)) & 1) == 1;
    }

    public void remove(long i) {
        int r = (int) (i / 32);
        int c = (int) (i % 32);
        longs[r] = (int) (longs[r] & (((1 << (c + 1)) - 1) ^ MAX));
    }

}
