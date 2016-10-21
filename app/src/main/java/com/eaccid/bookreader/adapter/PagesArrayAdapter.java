package com.eaccid.bookreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.eaccid.bookreader.appactivity.AppDatabaseManager;
import com.eaccid.bookreader.wordgetter.OnWordTouchListener;
import com.eaccid.bookreader.wordgetter.WordFromText;
import com.eaccid.bookreader.R;
import com.eaccid.bookreader.WordTranslatorViewer;
import com.eaccid.bookreader.wordgetter.WordOnTexvViewFinder;

import java.util.List;


public class PagesArrayAdapter extends ArrayAdapter {

    private Context mContext;
    private int mTextOnPage;
    private int viewItemLayout;
    private List<String> mPagesList;

    public PagesArrayAdapter(Context context, int textViewResourceId, List<String> pagesList) {
        super(context, R.layout.text_on_page, textViewResourceId, pagesList);
        this.mContext = context;
        this.mTextOnPage = textViewResourceId;
        this.mPagesList = pagesList;
        this.viewItemLayout = R.layout.text_on_page;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolderItem;

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(viewItemLayout, parent, false);


            viewHolderItem = new ViewHolderItem();
            viewHolderItem.textViewItem = (TextView) convertView.findViewById(mTextOnPage);
            viewHolderItem.pageNumber = (TextView) convertView.findViewById(R.id.page_number);

            viewHolderItem.textViewItem.setOnTouchListener(new OnWordTouchListener(position + 1));

            convertView.setTag(viewHolderItem);

        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }

        String textOnPage = mPagesList.get(position);
        viewHolderItem.textViewItem.setText(textOnPage);
        viewHolderItem.pageNumber.setText(
                String.valueOf(position + 1) +
                        " - " +
                        String.valueOf(mPagesList.size())
        );

        return convertView;

    }

    private static class ViewHolderItem {
        TextView textViewItem;
        TextView pageNumber;
    }

}





