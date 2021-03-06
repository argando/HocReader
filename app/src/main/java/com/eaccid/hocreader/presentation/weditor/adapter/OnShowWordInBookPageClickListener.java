package com.eaccid.hocreader.presentation.weditor.adapter;

import android.view.View;
import android.widget.Toast;

import com.eaccid.hocreader.provider.db.words.WordItem;

public class OnShowWordInBookPageClickListener implements View.OnClickListener {

    private final WordItem wordProvider;

    public OnShowWordInBookPageClickListener(WordItem wordToShowInPage) {
        this.wordProvider = wordToShowInPage;
    }

    @Override
    public void onClick(View v) {
        //TODO: define an interface, f.i. InteractivePage
        Toast.makeText(v.getContext(), "Under development", Toast.LENGTH_SHORT).show();
    }

}
