package com.eaccid.hocreader.data.remote.libtranslator.dictionary;

public interface Dictionary {

    @Deprecated
    boolean authorize(String login, String password);

    @Deprecated
    boolean isAuth();

    @Deprecated
    boolean addWord(String word, String textTranslation, String context);

}
