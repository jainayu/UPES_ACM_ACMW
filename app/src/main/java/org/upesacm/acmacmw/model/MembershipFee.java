package org.upesacm.acmacmw.model;

public class MembershipFee {
    public String PREMIUM_TYPE = "PREMIUM";
    public String ONE_YEAR_TYPE="ONE YEAR";
    public String TWO_YEAR_TYPE = "TWO YEARS";
    private String premiumFee, oneYearFee, twoYearFee;
    private String premiumMsg, oneYearMsg, twoYearsMsg;

    public MembershipFee() {
       /* setOneYearMsg();
        setPremiumMsg();
        setTwoYearsMsg();*/
    }

   /* public MembershipFee(String oneYearFee, String premiumFee, String twoYearFee) {
        this.premiumFee = premiumFee;
        this.oneYearFee = oneYearFee;
        this.twoYearFee = twoYearFee;
        setOneYearMsg();
        setPremiumMsg();
        setTwoYearsMsg();
    }*/



    public void setPremiumFee(String premiumFee) {
        this.premiumFee = premiumFee;
        setPremiumMsg();
    }
    public String getPremiumFee() {
        return premiumFee;
    }

    public String getOneYearFee() {
        return oneYearFee;
    }

    public void setOneYearFee(String oneYearFee) {
        this.oneYearFee = oneYearFee;
        setOneYearMsg();
    }

    public String getTwoYearFee() {
        return twoYearFee;
    }

    public void setTwoYearFee(String twoYearFee) {
        this.twoYearFee = twoYearFee;
        setTwoYearsMsg();
    }

    public String getPremiumMsg() {
        return premiumMsg;
    }

    private void setPremiumMsg() {
        this.premiumMsg = "Premium (\u20B9"+ getPremiumFee() +" for 4 Years + 1 Year International)";
    }

    public String getOneYearMsg() {
        return oneYearMsg;
    }

    private void setOneYearMsg() {
        this.oneYearMsg = "1 year membership(\u20B9"+getOneYearFee()+")";
    }

    public String getTwoYearsMsg() {
        return twoYearsMsg;
    }

    private void setTwoYearsMsg() {
        this.twoYearsMsg = "2 year membership (\u20B9"+getTwoYearFee()+")";
    }
}
