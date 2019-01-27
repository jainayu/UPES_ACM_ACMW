package org.upesacm.acmacmw.model;


public class Team {
    private String otp;
    private int amount;
    private String recipient;
    private boolean confirmed;

    private Team() {

    }

    public String getOtp() {
        return otp;
    }

    public int getAmount() {
        return amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public static class Builder {
        private String otp;
        private int amount;
        private String recipient;
        private boolean confirmed;

        public Team build() {
            Team team = new Team();
            team.otp = otp;
            team.amount = amount;
            team.recipient = recipient;
            team.confirmed = confirmed;

            return team;
        }

        public Builder setOtp(String otp) {
            this.otp = otp;
            return this;
        }

        public Builder setRecipient(String recipient) {
            this.recipient = recipient;
            return this;
        }
        public Builder setAmount(int amount) {
            this.amount = amount;
            return this;
        }
        public Builder setConfirmed(boolean confirmed) {
            this.confirmed = confirmed;
            return this;
        }
    }
}
