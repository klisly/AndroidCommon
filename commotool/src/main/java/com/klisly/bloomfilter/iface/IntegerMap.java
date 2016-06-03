/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.bloomfilter.iface;

public class IntegerMap implements BitMap {

    private static final int MAX = Integer.MAX_VALUE;
    private int[] ints = null;

    public IntegerMap() {
        ints = new int[93750000];
    }

    public IntegerMap(int size) {
        ints = new int[size];
    }

    public void add(long i) {
        int r = (int) (i / 32);
        int c = (int) (i % 32);
        ints[r] = ints[r] | (1 << c);
    }

    public boolean contains(long i) {
        int r = (int) (i / 32);
        int c = (int) (i % 32);
        return ((int) ((ints[r] >>> c)) & 1) == 1;
    }

    public void remove(long i) {
        int r = (int) (i / 32);
        int c = (int) (i % 32);
        ints[r] = ints[r] & (((1 << (c + 1)) - 1) ^ MAX);
    }

}
