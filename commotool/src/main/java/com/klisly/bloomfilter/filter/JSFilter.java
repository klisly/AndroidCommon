/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.klisly.bloomfilter.filter;


import com.klisly.bloomfilter.iface.BitMap;
import com.klisly.bloomfilter.iface.Filter;
import com.klisly.bloomfilter.iface.IntegerMap;
import com.klisly.bloomfilter.iface.LongMap;

public class JSFilter implements Filter {

    private BitMap bm = null;

    private long size = 0;

    public JSFilter(long maxValue, int MACHINENUM) throws Exception {
        this.size = maxValue;
        if (MACHINENUM == 32) {
            bm = new IntegerMap((int) (size / MACHINENUM));
        } else if (MACHINENUM == 64) {
            bm = new LongMap((int) (size / MACHINENUM));
        } else {
            throw new Exception("传入的机器位数有误");
        }
    }

    public JSFilter(long maxValue) {
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
        int hash = 1315423911;

        for (int i = 0; i < str.length(); i++) {
            hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));
        }

        if (hash < 0) {
            hash *= -1;
        }

        return hash % size;
    }

    @Override
    public void remove(String str) {
        long hash = this.myHashCode(str);
        bm.remove(hash);
    }

}
