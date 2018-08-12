package org.upesacm.acmacmw.util;

public class MemberIDGenerator {

    public static String generate(String sap) {
        String id="ACM"+sap.substring(4);
        System.out.println("ACM id "+id);

        return id;
    }
}
