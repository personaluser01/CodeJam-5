package com.eric.codejam;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BruteForce {
    
    final static Logger log = LoggerFactory.getLogger(Main.class);
    
    public static int count(int l, int r) {
        int ret = 0;
        
        for(int lb = l; lb <= r; ++lb) {
            for(int up = lb; up <= r; ++up) {
                int count = 0;
                for(int i = lb; i <= up; ++i) {
                    String s = Integer.toString(i, 10);
                   // log.debug("String {}", s);
                    if (s.endsWith(StringUtils.reverse(s.substring(0, s.length() / 2)))) {
                        ///log.debug("Is palin");
                        ++count;
                    }
                }
                
                if (count >= 0 && count % 2 == 0) {
                    ++ret;
                }
            }
        }
        
        return ret;
    }
}