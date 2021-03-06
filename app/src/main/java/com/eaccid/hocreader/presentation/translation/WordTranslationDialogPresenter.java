package com.eaccid.hocreader.presentation.translation;

import android.media.MediaPlayer;
import android.util.Log;

import com.eaccid.hocreader.data.remote.libtranslator.translator.TextTranslation;
import com.eaccid.hocreader.presentation.translation.semantic.MediaPlayerManager;
import com.eaccid.hocreader.presentation.translation.semantic.ImageViewManager;
import com.eaccid.hocreader.provider.fromtext.WordFromText;
import com.eaccid.hocreader.provider.fromtext.WordFromTextImpl;
import com.eaccid.hocreader.provider.translator.HocTranslatorProvider;
import com.eaccid.hocreader.provider.translator.TranslatedWordImpl;
import com.eaccid.hocreader.presentation.BasePresenter;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WordTranslationDialogPresenter implements BasePresenter<WordTranslationDialogFragment> {
    private final String LOG_TAG = "TranslationPresenter";
    private WordTranslationDialogFragment mView;

    private TranslatedWordImpl mTranslatedWordImpl;
    private MediaPlayer mMediaPlayer;
    private String mNextWordToTranslate;
    private Subscription mTranslationSubscription;

    @Override
    public void attachView(WordTranslationDialogFragment wordTranslationDialogFragment) {
        mView = wordTranslationDialogFragment;
        Log.i(LOG_TAG, "TranslationPresenter has been attached.");
    }

    @Override
    public void detachView() {
        Log.i(LOG_TAG, "TranslationPresenter has been detached.");
        mView = null;
        if (mMediaPlayer != null) mMediaPlayer.release();
        if (mTranslationSubscription != null) mTranslationSubscription.unsubscribe();
    }

    public void onViewCreated() {
        translateText(mView.getWordFromText());
    }

    private void translateText(WordFromText wordFromText) {
        mView.showContextWord(wordFromText.getText());
        if (mTranslationSubscription != null && !mTranslationSubscription.isUnsubscribed()) {
            mTranslationSubscription.unsubscribe();
        }
        mTranslationSubscription = new HocTranslatorProvider()
                .translate(wordFromText.getText())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TextTranslation>() {
                    @Override
                    public void onCompleted() {
                        mView.notifyTranslationsChanged();
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        unsubscribe();
                    }

                    @Override
                    public void onNext(TextTranslation textTranslation) {
                        {
                            Log.i(LOG_TAG, "Text translated status: " + !textTranslation.isEmpty());
                            if (isNextWordToTranslateEmpty())
                                setmNextWordToTranslate(textTranslation.getWord());
                            showTranslationsData(textTranslation);
                            mTranslatedWordImpl = new TranslatedWordImpl(wordFromText.getText(),wordFromText.getSentence());
                        }
                    }
                });
    }

    private void showTranslationsData(TextTranslation textTranslation) {

        mView.showContextWord(mView.getWordFromText().getText());
        mView.showBaseWord(mNextWordToTranslate);

        new ImageViewManager()
                .loadPictureFromUrl(mView.getWordPicture(), textTranslation.getPicUrl());
        mMediaPlayer = new MediaPlayerManager()
                .createAndPreparePlayerFromURL(textTranslation.getSoundUrl());
        mView.showWordTranscription(textTranslation.getTranscription());
        mView.showTranslations(textTranslation.getTranslates());
    }

    public void OnSpeakerClicked() {
        if (mMediaPlayer == null) return;
        new MediaPlayerManager().play(mMediaPlayer);
        mMediaPlayer.setOnCompletionListener(mp -> mView.showSpeaker(false));
        mView.showSpeaker(true);
    }

    public void OnWordClicked() {
        String nextWord = getNextWordToTranslate();
        WordFromTextImpl currentWord = mView.getWordFromText();
        setmNextWordToTranslate(currentWord.getText());
        currentWord.setText(nextWord);
        translateText(currentWord);
    }

    public void onTranslationClick(String text) {
        mTranslatedWordImpl.addTranslation(text);
        ((WordTranslationDialogFragment.OnWordTranslationClickListener) mView.getContext())
                .onWordTranslated(mTranslatedWordImpl);
        mView.dismiss();
    }

    private void setmNextWordToTranslate(String mNextWordToTranslate) {
        this.mNextWordToTranslate = mNextWordToTranslate;
    }

    private String getNextWordToTranslate() {
        return mNextWordToTranslate;
    }

    public boolean isNextWordToTranslateEmpty() {
        return mNextWordToTranslate == null || mNextWordToTranslate.isEmpty();
    }
}
