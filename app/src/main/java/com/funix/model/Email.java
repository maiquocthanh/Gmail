package com.funix.model;

import javax.mail.Message;

/**
 * Created by hung on 6/12/2017.
 */

public class Email {
    String subject;
    String sender;
    String time;
    Message message;

    public Email(String subject, String sender, String time, Message message) {
        this.subject = subject;
        this.sender = sender;
        this.time = time;
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public String getSender() {
        return sender;
    }

    public String getTime() {
        return time;
    }

    public Message getMessage() {
        return message;
    }
}
