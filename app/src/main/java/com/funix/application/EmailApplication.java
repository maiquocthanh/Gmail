package com.funix.application;

import android.app.Application;
import android.util.Log;

import com.funix.manager.EmailConfig;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

/**
 * Created by hung on 6/12/2017.
 */

public class EmailApplication extends Application {
    private static final String TAG = EmailApplication.class.getSimpleName();
    private Message mMessage;
    private Store mStore;

    public EmailApplication() {
        super();
        EmailConfig receiver = new EmailConfig();
        Store store =null;
        try{
            store = receiver.getStore();

        }catch (MessagingException e){
            Log.e(TAG,"Store error");

        }
        mStore = store;

    }

    public Message getMessage() {
        return mMessage;
    }

    public Store getStore() {
        return mStore;
    }
    public  void  setMessage(Message message){
        this.mMessage =message;
    }
}
