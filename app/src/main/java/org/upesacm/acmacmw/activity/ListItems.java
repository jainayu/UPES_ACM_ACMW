package org.upesacm.acmacmw.activity;

public class ListItems {
    private static String rank1;
    private static String name1;
    private static String score1;

    public ListItems(String rank1, String name1, String score1) {
        this.rank1 = rank1;
        this.name1 = name1;
        this.score1 = score1;
    }

    public static String getRank1() {
        return rank1;
    }

    public static String getName1() {
        return name1;
    }

    public static String getScore1() {
        return score1;
    }
}
