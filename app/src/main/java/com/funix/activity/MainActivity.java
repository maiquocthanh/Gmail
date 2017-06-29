package com.funix.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.funix.R;
import com.funix.application.EmailApplication;
import com.funix.model.Email;
import com.funix.util.Constant;
import com.funix.util.Utils;
import com.funix.view.EmailAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

/**
 * Created by hung on 6/13/2017.
 */


public class MainActivity extends AppCompatActivity {
    private boolean isNetworkAvailable = false;
    private boolean isConfigured = false;
    private boolean isReset = false;
    private String mEmail;
    private String mPassword;
    private SharedPreferences mSharePreferences;
    private TextView mEmailTextView;
    private ArrayList<Email> mFetchEmails;
    private EmailAdapter mAdapter;
    private ListView mEmailList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Click on floadActionButton to next sendingEmailActivity
        floatSendEmail();
        //Click here to next read email
       itemClickReadMail();

    }

    //create a method to set onItemClickListener to read email and reply email
    public void itemClickReadMail() {
        mSharePreferences = getSharedPreferences(Constant.KEY_SHARE_PREFERENCES, MODE_PRIVATE);
        mFetchEmails = new ArrayList<>();
        mAdapter = new EmailAdapter(this, mFetchEmails);
        mEmailList = (ListView) findViewById(R.id.email_list);
        mEmailList.setAdapter(mAdapter);
        mEmailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((EmailApplication) getApplication()).setMessage(mFetchEmails.get(position).getMessage());
                Intent myIntent = new Intent(MainActivity.this, ReadEmailActivity.class);
                myIntent.putExtra(Constant.EMAIL_SENDER, mFetchEmails.get(position).getSender());
                myIntent.putExtra(Constant.EMAIL_TITLE, mFetchEmails.get(position).getSubject());
                myIntent.putExtra(Constant.EMAIL_ADDRESS, mEmail);
                myIntent.putExtra(Constant.EMAIL_PASSWORD, mPassword);

                startActivityForResult(myIntent, 0);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvailable = Utils.isNetworkAvailable(this);

        isConfigured = mSharePreferences.getBoolean(Constant.EMAIL_CONFIGURED, false);

        if (isConfigured) {
            mEmail = mSharePreferences.getString(Constant.EMAIL_ADDRESS, "");
            mPassword = mSharePreferences.getString(Constant.EMAIL_PASSWORD, "");
            if (mEmailTextView != null) {
                mEmailTextView.setText(mEmail);
            }
            getAllEmail();
        } else if (isNetworkAvailable) {
            dialogLogin();
        } else {
            Toast.makeText(this, "Can not connect to internet", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater menuInflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.id_logout) {
            logout();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        isReset = true;
        isConfigured = false;
        mSharePreferences.edit().clear().apply();
        mFetchEmails.clear();
        mAdapter.notifyDataSetChanged();
        //create a handler object to delay
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogLogin();
            }
        }, 500);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                getAllEmail();
            }
        }
    }

    // Create a method to get all emaill
    private void getAllEmail() {
        AsyncTask<Object, Object, ArrayList<Email>[]> task = new AsyncTask<Object, Object, ArrayList<Email>[]>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.loading_layout);
                linearLayout.setVisibility(View.VISIBLE);
            }


            @Override
            protected ArrayList<Email>[] doInBackground(Object... voids) {

                ArrayList<Email> emails = new ArrayList<>();
                Message[] messages;
                try {
                    Store store = ((EmailApplication) getApplication()).getStore();
                    if (isReset) {
                        store.close();
                        isReset = false;
                    }
                    if (!store.isConnected()) {
                        store.connect(mEmail, mPassword);
                    }

                    Folder inbox = store.getFolder("INBOX");
                    if (!inbox.isOpen()) {
                        inbox.open(Folder.READ_ONLY);
                    }

                    messages = inbox.getMessages();
                } catch (AuthenticationFailedException e) {
                    return (ArrayList<Email>[]) new ArrayList[2];

                } catch (Exception e) {
                    return (ArrayList<Email>[]) new ArrayList[3];
                }
                if (messages.length > 15) {
                    messages = Arrays.copyOfRange(messages, messages.length - 15, messages.length);
                }

                SimpleDateFormat format = new SimpleDateFormat("dd-MM", Locale.ENGLISH);
                InternetAddress address;
                for (Message msg : messages) {
                    try {
                        Date date = msg.getReceivedDate();
                        address = (InternetAddress) msg.getFrom()[0];
                        emails.add(0, new Email(msg.getSubject(), address.getAddress(), format.format(date), msg));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new ArrayList[]{emails};
            }


            @Override
            protected void onPostExecute(ArrayList<Email>[] emailData) {

                ArrayList<Email> emails = emailData[0];
                if (emails == null) {
                    if (emailData.length == 2) {
                        MainActivity.this.logout();
                    } else {
                        Toast.makeText(MainActivity.this, "Connection error", Toast.LENGTH_LONG).show();
                    }
                } else if (emails.isEmpty()) {
                    Toast.makeText(MainActivity.this, "MailBox is empty", Toast.LENGTH_LONG).show();

                } else {
                    MainActivity.this.notifyUpdateData(emails);
                }
                LinearLayout linearLayout = (LinearLayout) MainActivity.this.findViewById(R.id.loading_layout);
                linearLayout.setVisibility(View.GONE);
            }
        };
        task.execute();
    }

    // create a dialog to login your gmail account
    private void dialogLogin() {
        //create a dialog object
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Dialog);
        dialog.setContentView(R.layout.dialog_login);
        dialog.setTitle("Enter your account");
        dialog.setCanceledOnTouchOutside(false);

        // set on click listen to login
        Button loginButton = (Button) dialog.findViewById(R.id.input_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get information of email and password
                mEmail = ((EditText) dialog.findViewById(R.id.email_text)).getText().toString();
                mPassword = ((EditText) dialog.findViewById(R.id.pass_text)).getText().toString();
                dialog.dismiss();
                //invoked saveConfig to pass the email and the password
                saveConfig(mEmail, mPassword);
                // invoked getAllEmail
                getAllEmail();
            }
        });
        dialog.show();

    }

    private void saveConfig(String email, String password) {
        SharedPreferences.Editor editor = mSharePreferences.edit();
        editor.putString(Constant.EMAIL_ADDRESS, email);
        editor.putString(Constant.EMAIL_PASSWORD, password);
        editor.putBoolean(Constant.EMAIL_CONFIGURED, true);
        editor.clear().apply();
        if (mEmailTextView != null) {
            mEmailTextView.setText(email);
        }

    }

    // create  a mothed to add all email into ArrayList
    private void notifyUpdateData(ArrayList<Email> emails) {
        isConfigured = true;
        mFetchEmails.clear();
        mFetchEmails.addAll(emails);
        mAdapter.notifyDataSetChanged();

    }


    private void floatSendEmail() {
        FloatingActionButton fab_send = (FloatingActionButton) findViewById(R.id.fab_send_email);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable) {
                    Toast.makeText(MainActivity.this, "Network is not available", Toast.LENGTH_LONG).show();
                } else if (!isConfigured) {
                    Toast.makeText(MainActivity.this, "Config mEmail first", Toast.LENGTH_LONG).show();
                } else {
                    Intent myIntent = new Intent(MainActivity.this, SendEmailActivity.class);
                    myIntent.putExtra(Constant.EMAIL_ADDRESS, mEmail);
                    myIntent.putExtra(Constant.EMAIL_PASSWORD, mPassword);
                    startActivity(myIntent);
                }
            }
        });
    }
}
