package org.upesacm.acmacmw.util;

import java.util.Calendar;
import java.util.Random;

public class RandomOTPGenerator {

    public static String generate(int seed,int length) {
        Random random=new Random(seed+ System.nanoTime());

        String capitalAlphabets="012345789";
        String smallAlphabets="0123456789";
        String digits="0123456789";

        String values=capitalAlphabets+smallAlphabets+digits;
        String otp="";
        for(int i=0;i<length;i++) {
            otp+=values.charAt(random.nextInt(values.length()));
        }
        return otp;
    }
}
