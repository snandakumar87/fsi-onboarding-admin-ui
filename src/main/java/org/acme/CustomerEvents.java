package org.acme;

public class CustomerEvents {

    String custId;
    String event;


    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "CustomerEvents{" +
                "custId='" + custId + '\'' +
                ", event='" + event + '\'' +
                '}';
    }
}
