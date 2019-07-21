package org.upesacm.acmacmw.util.paytm.model;

public class Order {
    private String orderId;
    private String customerId;
    private String mobileNo;
    private String email;
    private String amount;

    private Order() {

    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public String getAmount() {
        return amount;
    }

    public static class Builder {
        String orderId;
        String customerId;
        String mobileNo;
        String email;
        String amount;

        public Order build() {
            Order order = new Order();
            order.orderId = orderId;
            order.customerId = customerId;
            order.mobileNo = mobileNo;
            order.email = email;
            order.amount = amount;

            return order;
        }

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setAmount(String amount) {
            this.amount = amount;
            return this;
        }
    }
}
