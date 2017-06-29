package com.funix.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.funix.R;
import com.funix.model.Email;

import java.util.ArrayList;

/**
 * Created by hung on 6/12/2017.
 */

public class EmailAdapter extends ArrayAdapter<Email> {
    public EmailAdapter(Context context, ArrayList<Email> emails) {
        super(context, 0, emails);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.email_row, parent, false);
            viewHolder.senderView = (TextView) convertView.findViewById(R.id.sender_text);
            viewHolder.subject = (TextView) convertView.findViewById(R.id.subject_text);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time_text);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();

        }

        final Email email = getItem(position);
        viewHolder.senderView.setText(email.getSender());
        viewHolder.subject.setText(email.getSubject());
        viewHolder.time.setText(email.getTime());
        return convertView;
    }

 private class ViewHolder {
        TextView senderView;
        TextView subject;
        TextView time;
    }
}
