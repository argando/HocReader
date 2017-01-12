package com.eaccid.hocreader.provider.fromtext;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.MotionEvent;
import android.widget.TextView;

import com.eaccid.hocreader.underdevelopment.spann.WordClickableSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordFromTextProvider {

    private int charStart = 0;

    public WordFromText getWordByMotionEvent(TextView tv, MotionEvent event) {
        WordFromText wordFromText = new WordFromText();
        Layout textLayout = tv.getLayout();
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (textLayout != null) {
            int currentLineOfClickedChar = textLayout.getLineForVertical(y);
            int charOffsetInLine = textLayout.getOffsetForHorizontal(currentLineOfClickedChar, x);
            int startOfLine = textLayout.getLineStart(currentLineOfClickedChar);
            int endOfLine = textLayout.getLineEnd(currentLineOfClickedChar);
            CharSequence text = tv.getText();
            String wordFromLine = getWordFromLine(text, startOfLine, charOffsetInLine, endOfLine);
            wordFromText.setText(wordFromLine);
//            temp start
            Spannable spanText = new SpannableString(text);
            spanText.setSpan(new WordClickableSpan(tv.getContext(), wordFromLine, charStart), charStart, charStart + wordFromLine.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(spanText, TextView.BufferType.SPANNABLE);
//            temp end

            wordFromText.setSentence(
                    getSentenceFromText(
                            text, text.subSequence(startOfLine, endOfLine).toString()));
//            wordFromText.setSentence(text.subSequence(startOfLine, endOfLine).toString());
        }
        return wordFromText;
    }

    private String getWordFromLine(CharSequence text, int startOfLine, int offset, int endOfLine) {
        String firstPartOfWord = getFirstPartOfWordInLine(text.subSequence(startOfLine, offset));
        String lastPartOfWord = getLastPartOfWordInLine(text.subSequence(offset, endOfLine));

        charStart = offset - firstPartOfWord.length();

        return firstPartOfWord + lastPartOfWord;
    }

    private String getFirstPartOfWordInLine(CharSequence sublineBeforeClickedChar) {
        return getMatchingResult(sublineBeforeClickedChar, Pattern.compile("(\\w+)$"));
    }

    private String getLastPartOfWordInLine(CharSequence sublineAfterClickedChar) {
        return getMatchingResult(sublineAfterClickedChar, Pattern.compile("^([\\w\\-]+)"));
    }

    private String getSentenceFromText(CharSequence text, CharSequence subtext) {
        return getMatchingResult(text, Pattern.compile("(?<=\\.)(\\w|\\s|,|'|`)+" + subtext + ".+?(\\.)"));
    }

    private String getMatchingResult(CharSequence line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        try {
            if (matcher.find()) {
                return matcher.group(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
