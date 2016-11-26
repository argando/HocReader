package com.eaccid.bookreader;


import com.eaccid.bookreader.underdev.settings.LingualeoServiceCookiesHandler;
import com.eaccid.libtranslator.lingualeo.dictionary.LingualeoDictionary;
import com.eaccid.libtranslator.lingualeo.translator.LingualeoTranslator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OutLingualeoUnitTest {

    private static String cookies = "";

    @Mock
    LingualeoServiceCookiesHandler mLeoCookies;

    @Before
    public void setUp() {
        when(
                mLeoCookies.loadCookies()
        ).thenReturn(
                cookies);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                cookies = (String) args[0];
                return null;
            }
        }).when(mLeoCookies).storeCookies(anyString());

    }

    @Test
    public void test_translation() throws Exception {
        LingualeoTranslator translator = new LingualeoTranslator();
        Assert.assertTrue("Word has been translated.", translator.translate("mad"));
    }

    @Test
    public void test_authorize() throws Exception {

        LingualeoDictionary dictionary = new LingualeoDictionary(mLeoCookies);
        boolean isAuth = dictionary.authorize("accidental899@mail.ru", "accid899");
       Assert.assertTrue("Authorization on Lingualeo.", isAuth);
    }


    @Test
    public void test_addWord() throws Exception {

        LingualeoDictionary dictionary = new LingualeoDictionary(mLeoCookies);

        boolean isAdded = dictionary.addWord("map", "отображение", "");
        Assert.assertTrue("Is Auth on Lingualeo.", isAdded);
    }

}