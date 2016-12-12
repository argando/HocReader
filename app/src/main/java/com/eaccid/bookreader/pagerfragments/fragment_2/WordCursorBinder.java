package com.eaccid.bookreader.pagerfragments.fragment_2;

import android.content.Context;
import android.database.Cursor;
import com.eaccid.hocreader.data.local.WordFilter;
import com.eaccid.hocreader.data.local.WordManager;
import com.eaccid.hocreader.data.local.db.entity.Word;
import com.j256.ormlite.android.apptools.OrmLiteCursorLoader;
import com.j256.ormlite.stmt.PreparedQuery;

public class WordCursorBinder {

    private Context context;
    private boolean filterByBook;

    public WordCursorBinder(Context context, boolean filterByBook) {
        this.context = context;
        this.filterByBook = filterByBook;
    }

    public OrmliteCursorRecyclerViewAdapter createAdapterWithCursor(DrawerRecyclerViewAdapter adapter, WordManager wordManager) {


        if (filterByBook) {
            wordManager.setFilter(WordFilter.BY_BOOK);
        }

        PreparedQuery<Word> pq = wordManager.getWordPreparedQuery(null, null);
        OrmLiteCursorLoader<Word> liteCursorLoader = new OrmLiteCursorLoader<>(
                context,
                wordManager.getWordDao(),
                pq);
        Cursor cursor = liteCursorLoader.loadInBackground();
        adapter.changeCursor(cursor, pq);
        return adapter;
    }

}