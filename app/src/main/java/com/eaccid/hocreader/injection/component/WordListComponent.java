package com.eaccid.hocreader.injection.component;

import com.eaccid.hocreader.injection.WordListScope;
import com.eaccid.hocreader.injection.module.DataProviderModule;
import com.eaccid.hocreader.presentation.pager.PagerPresenter;
import com.eaccid.hocreader.presentation.weditor.adapter.SwipeOnLongPressRecyclerViewAdapter;
import com.eaccid.hocreader.presentation.weditor.WordEditorPresenter;
import com.eaccid.hocreader.provider.db.words.WordListInteractor;
import com.eaccid.hocreader.presentation.weditor.action.ToolbarActionModeCallback;

import dagger.Subcomponent;

@Subcomponent(modules = DataProviderModule.class)
@WordListScope
public interface WordListComponent {

    WordListInteractor wordListInteractor();

    void inject(PagerPresenter pagerPresenter);

    void inject(WordEditorPresenter wordEditorPresenter);

    void inject(SwipeOnLongPressRecyclerViewAdapter swipeOnLongPressRecyclerViewAdapter);

    void inject(ToolbarActionModeCallback toolbar_actionMode_);

}
