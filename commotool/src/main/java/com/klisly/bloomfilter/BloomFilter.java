package com.klisly.bloomfilter;

import java.io.Serializable;

import com.klisly.bloomfilter.filter.JSFilter;
import com.klisly.bloomfilter.filter.JavaFilter;
import com.klisly.bloomfilter.filter.PHPFilter;
import com.klisly.bloomfilter.filter.PJWFilter;
import com.klisly.bloomfilter.filter.SDBMFilter;
import com.klisly.bloomfilter.iface.Filter;

public class BloomFilter implements Serializable {

    private static int length = 5;

    Filter[] filters = new Filter[length];

    public BloomFilter(int m) {
        float mNum = m / 5;
        long size = (long) (1L * mNum * 1024 * 8);
        filters[0] = new JavaFilter(size);
        filters[1] = new PHPFilter(size);
        filters[2] = new JSFilter(size);
        filters[3] = new PJWFilter(size);
        filters[4] = new SDBMFilter(size);
    }

    public void remove(String str) {
        for (int i = 0; i < length; i++) {
            filters[i].remove(str);
        }
    }

    public void add(String str) {
        for (int i = 0; i < length; i++) {
            filters[i].add(str);
        }
    }

    public boolean contains(String str) {
        for (int i = 0; i < length; i++) {
            if (filters[i].contains(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAndAdd(String str) {
        boolean flag = true;
        for (int i = 0; i < length; i++) {
            flag = flag && filters[i].containsAndAdd(str);
        }
        return flag;
    }
}
