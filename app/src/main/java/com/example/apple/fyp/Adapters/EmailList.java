package com.example.apple.fyp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.apple.fyp.Objects.EMailObject;
import com.example.apple.fyp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malik on 4/25/2018.
 */

public class EmailList extends BaseAdapter {
    List<EMailObject> list;
    Context context;

    public EmailList(List<EMailObject> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderEmail viewHolderEmail;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.email_item_design, null, false);
            viewHolderEmail = new ViewHolderEmail(convertView);
            convertView.setTag(viewHolderEmail);
        } else {
            viewHolderEmail = (ViewHolderEmail) convertView.getTag();
        }
        viewHolderEmail.tvEmail.setText(list.get(position).getFrom());
        viewHolderEmail.tvEmailSubject.setText(list.get(position).getSubject());
        viewHolderEmail.tvCenterWord.setText(list.get(position).getFrom().charAt(0) + "");
        return convertView;
    }

    class ViewHolderEmail {
        TextView tvEmail, tvEmailSubject, tvCenterWord;

        public ViewHolderEmail(View view) {
            tvEmail = (TextView) view.findViewById(R.id.txtItemEmailFrom);
            tvEmailSubject = (TextView) view.findViewById(R.id.txtItemEmailSubject);
            tvCenterWord = (TextView) view.findViewById(R.id.txtItemEmailCenterWord);
        }
    }
}
