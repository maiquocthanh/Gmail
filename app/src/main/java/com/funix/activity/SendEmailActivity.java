package com.funix.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.funix.R;
import com.funix.manager.SendMail;
import com.funix.util.Constant;
import com.funix.util.Utils;

public class SendEmailActivity extends AppCompatActivity {
    private EditText receiverText, titleText, contentText;
    String receiver, title, content, email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Compose email");
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        receiverText = (EditText) findViewById(R.id.receiver_text);
        titleText = (EditText) findViewById(R.id.title_text);
        contentText =(EditText) findViewById(R.id.content_text);
        Intent  intent = getIntent();
        if(intent.hasExtra(Constant.EMAIL_ADDRESS)){
            email = intent.getStringExtra(Constant.EMAIL_ADDRESS);
            password = intent.getStringExtra(Constant.EMAIL_PASSWORD);

        }
        if(intent.hasExtra(Constant.EMAIL_REPLY)){
            receiverText.setText(intent.getStringExtra(Constant.EMAIL_REPLY_TO));
            title = intent.getStringExtra(Constant.EMAIL_TITLE);
            if(!title.toLowerCase().startsWith("re: ")){
                title = "Re: " + intent.getStringExtra(Constant.EMAIL_TITLE);
            }
            titleText.setText(title);
            receiverText.setFocusable(false);
            titleText.setFocusable(false);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_activity, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_send_email) {
           sendEmail();
            return true;

        }
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendEmail() {
        receiver = receiverText.getText().toString();
        title = titleText.getText().toString();
        content = contentText.getText().toString();
        if(receiver.isEmpty()|| !Utils.validateEmail(receiver)){
            Toast.makeText(this,"Email is invalid",Toast.LENGTH_LONG).show();

        }else {
            SendMail sendMail = new SendMail(this, receiver, title, content, email, password);
            sendMail.execute();
        }

    }

}
