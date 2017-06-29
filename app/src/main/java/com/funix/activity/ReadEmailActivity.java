package com.funix.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.funix.R;
import com.funix.application.EmailApplication;
import com.funix.util.Constant;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

/**
 * Created by hung on 6/13/2017.
 */

public class ReadEmailActivity extends AppCompatActivity {

    private static final String TAG = ReadEmailActivity.class.getSimpleName();
    private WebView mWebViewContent;
    private String mEmail, mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Read email");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Message message = ((EmailApplication) getApplication()).getMessage();
        //Invoked this method to reply email
        ReplyEmail();
        //Invoked this method to load contents from sender
        getContent(message);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void ReplyEmail() {

        final Intent intent = getIntent();
        final String title = "Subject: " + intent.getStringExtra(Constant.EMAIL_TITLE);
        final String sender = "Sender: " + intent.getStringExtra(Constant.EMAIL_SENDER);
        mEmail = intent.getStringExtra(Constant.EMAIL_ADDRESS);
        mPassword = intent.getStringExtra(Constant.EMAIL_PASSWORD);
        TextView detailTitleText = (TextView) findViewById(R.id.detail_title_text);
        TextView detailSenderText = (TextView) findViewById(R.id.detail_sender_text);
        mWebViewContent = (WebView) findViewById(R.id.detail_content_text);
        detailTitleText.setText(title);
        detailSenderText.setText(sender);

        final FloatingActionButton replyButton = (FloatingActionButton) findViewById(R.id.reply_button);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent(ReadEmailActivity.this, SendEmailActivity.class);
                replyIntent.putExtra(Constant.EMAIL_ADDRESS, mEmail);
                replyIntent.putExtra(Constant.EMAIL_PASSWORD, mPassword);
                replyIntent.putExtra(Constant.EMAIL_REPLY, true);
                replyIntent.putExtra(Constant.EMAIL_REPLY_TO, intent.getStringExtra(Constant.EMAIL_SENDER));
                replyIntent.putExtra(Constant.EMAIL_TITLE, intent.getStringExtra(Constant.EMAIL_TITLE));
                startActivity(replyIntent);

            }
        });
    }

    protected void getContent(final Message message) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(ReadEmailActivity.this, "Content is loading", "Please waiting...", false, false);
            }


            @Override
            protected String doInBackground(Void... params) {

                String result = "";
                try {
                    result = parseData(message);
                } catch (FolderClosedException e) {
                    try {
                        Store store = ((EmailApplication) getApplication()).getStore();
                        if (!store.isConnected()) {
                            store.connect(mEmail, mPassword);
                        }
                        Folder inBox = store.getFolder("INBOX");
                        if (!inBox.isOpen()) {
                            inBox.open(Folder.READ_ONLY);
                        }
                        result = parseData(inBox.getMessage(message.getMessageNumber()));
                    } catch (MessagingException | IOException e1) {
                        Log.e(TAG, "Can not reconnect to Folder");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Can not get message content");
                }
                return result;
            }

            @Override
            protected void onPostExecute(String content) {
                super.onPostExecute(content);
                progressDialog.dismiss();
                mWebViewContent.loadData(content, "text/html; charset=utf-8", "utf-8");

            }

            private String parseData(Message msg) throws MessagingException, IOException {
                String result = "";
                if (msg.isMimeType("text/plain")) {
                    result = msg.getContent().toString();

                } else if (msg.isMimeType("multipart/*")) {
                    MimeMultipart multipart = (MimeMultipart) msg.getContent();
                    int count = multipart.getCount();
                    for (int i = 0; i < count; i++) {
                        BodyPart part = multipart.getBodyPart(i);
                        if (part.isMimeType("text/html")) {
                            result += "\n" + part.getContent();

                        }
                    }

                }
                return result;
            }

        };
        task.execute();
    }
}

