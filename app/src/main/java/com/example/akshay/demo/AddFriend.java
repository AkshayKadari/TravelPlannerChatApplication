package com.example.akshay.demo;

/**
 * Created by akshay on 4/21/2017.
 */

public class AddFriend {
    String id;
    String status;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    String reqId;

    @Override
    public String toString() {
        return "userRequest{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", reqId='" + reqId + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
