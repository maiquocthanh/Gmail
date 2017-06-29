package com.funix.manager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by hung on 6/13/2017.
 */

public class SendMail extends AsyncTask<Void, Void, Boolean>{

    private Context mContext;
    private Session mSession;
    private String mEmail, mTitle, mContent, mFrom, mPassword;
    private ProgressDialog mProgress;

    public SendMail(Context context, String receiver, String title, String content, String from, String password) {
        this.mContext = context;
        this.mEmail = receiver;
        this.mTitle = title;
        this.mContent = content;
        this.mFrom = from;
        this.mPassword = password;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgress = ProgressDialog.show(mContext,"Sending email... ","Please wait!",false,false);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        mSession = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mFrom, mPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(mSession);
            message.setFrom(new InternetAddress(mFrom));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            message.setSubject(mTitle);
            message.setText(mContent);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }



    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        mProgress.dismiss();
        if (aVoid) {
            Toast.makeText(mContext, "Message has Sent", Toast.LENGTH_SHORT).show();
            if (mContext instanceof Activity) {
                ((Activity) mContext).finish();
            }
        } else {
            Toast.makeText(mContext, "Failed to send email", Toast.LENGTH_LONG).show();
        }

    }
}
