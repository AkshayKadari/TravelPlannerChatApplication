package com.example.akshay.demo;

/**
 * Created by akshay on 5/1/2017.
 */

public class statusTrip {
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "statusTrip{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                '}';
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

    String id;
    String status;


}
