package com.example.akshay.demo;

/**
 * Created by akshay on 5/2/2017.
 */

public class DeletedMessages {
    String msgid;

    String deleteduserid;

    public String getMessagedeleteid() {
        return messagedeleteid;
    }

    public void setMessagedeleteid(String messagedeleteid) {
        this.messagedeleteid = messagedeleteid;
    }

    String messagedeleteid;

    @Override
    public String toString() {
        return "DeletedMessages{" +
                "msgid='" + msgid + '\'' +
                ", deleteduserid='" + deleteduserid + '\'' +
                ", messagedeleteid='" + messagedeleteid + '\'' +
                '}';
    }

    public String getDeleteduserid() {
        return deleteduserid;
    }

    public void setDeleteduserid(String deleteduserid) {
        this.deleteduserid = deleteduserid;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

}
