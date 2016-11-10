package com.eaccid.bookreader.provider;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WordDatabaseProviderFragment extends Fragment {
    private WordDatabaseDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);  // keep the mDataProvider instance
        mDataProvider = new WordDatabaseDataProvider();

    }

    public WordDatabaseDataProvider getDataProvider() {
        return mDataProvider;
    }
}