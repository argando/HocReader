package com.eaccid.hocreader.data.local;

import android.support.annotation.Nullable;
import android.util.Log;

import com.eaccid.hocreader.data.local.db.entity.Book;
import com.eaccid.hocreader.data.local.db.entity.Word;
import com.eaccid.hocreader.data.local.db.service.BookDaoService;
import com.eaccid.hocreader.data.local.db.service.DatabaseManager;
import com.eaccid.hocreader.data.local.db.service.WordDaoService;
import com.eaccid.hocreader.exception.NotImplementedException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppDatabaseManager {

    private final String LOG_TAG = "AppDatabaseManager";
    private DatabaseManager mDatabaseManager;

    public AppDatabaseManager(DatabaseManager mDatabaseManager) {
        this.mDatabaseManager = mDatabaseManager;
    }

    private WordFilter currentFilter = WordFilter.NONE;
    private static Book currentBook;
    private static int currentPage = 1;

    public void setCurrentBookForAddingWord(String filePath) {
        try {
            BookDaoService bs = mDatabaseManager.getBookService();
            currentBook = (Book) bs.getById(filePath);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentPageForAddingWord(int pageNumber) {
        currentPage = pageNumber;
    }

    public String getCurrentBookName() {
        return currentBook.getName();
    }

    public String getCurrentBookPath() {
        return currentBook.getPath();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public WordFilter clearFilter() {
        return currentFilter = WordFilter.NONE;
    }

    public void setFilter(WordFilter filter) {
        currentFilter = filter;
    }

    /**
     * books table
     */

    public void refreshBooks(List<String> bookpaths) {
        try {
            BookDaoService bookDaoService = mDatabaseManager.getBookService();
            List<Book> booksInDB = bookDaoService.getAll();
            for (Book book : booksInDB
                    ) {
                if (!bookpaths.contains(book.getPath())) {
                    bookDaoService.delete(book);
                    Log.i(LOG_TAG, "book '" + book.getName() + "' has been deleted.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> getAllBooks() {
        try {
            BookDaoService bs = mDatabaseManager.getBookService();
            return bs.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void createOrUpdateBook(String bookPath, String bookName) {
        try {
            BookDaoService bs = mDatabaseManager.getBookService();
            Book book = new Book(bookPath, bookName);
            bs.createOrUpdate(book);
            Log.i(LOG_TAG, "book '" + book.getName() + "' has been created / updated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * words table
     */
    public void createOrUpdateWord(String wordname, String translation, String context,
                                   boolean enabledOnline) {
        Word word = new Word();
        word.setName(wordname);
        word.setTranslation(translation);
        word.setContext(context);
        word.setPage(currentPage);
        word.setEnabledOnline(enabledOnline);
        word.setBook(currentBook);
        try {
            WordDaoService ws = mDatabaseManager.getWordService();
            boolean succeed = ws.createOrUpdate(word);
            Log.i(LOG_TAG, "Word '" + word.getName() + "' has been created / updated: " + succeed);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteWord(Word word) {
        try {
            WordDaoService ws = mDatabaseManager.getWordService();
            ws.delete(word);
            Log.i(LOG_TAG, "word '" + word.getName() + "' has been deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //TODO create filter, temp solution
    public List<Word> getAllWords(@Nullable Iterable<String> wordsFilter, @Nullable String bookIdFilter) {

        List<Word> lw = new ArrayList<>();
        try {
            WordDaoService ws = mDatabaseManager.getWordService();
            switch (currentFilter) {
                case BY_BOOK:

                    if (bookIdFilter == null && currentBook == null)
                        throw new RuntimeException("Current book filter has not been set: \n" +
                                "'dataManager.setFilter( ? )' or argument 'bookIdFilter ?'");
                    lw = ws.getAllByBookId(bookIdFilter == null ? currentBook.getPath() : bookIdFilter);
                    break;
                case BY_PAGE:

                    lw = ws.getAllByBookIdAndPage(bookIdFilter == null ? currentBook.getPath() : bookIdFilter, currentPage);
                    break;

                case BY_BOOK_AND_WORD_COLLECTION:

                    if (wordsFilter == null)
                        return lw;
                    lw = ws.getAllByWordNameCollectionAndBookId(wordsFilter, false, currentBook.getPath());
                    break;

                case BY_BOOK_AND_EXCLUDED_WORD_COLLECTION:

                    if (wordsFilter == null)
                        return lw;
                    lw = ws.getAllByWordNameCollectionAndBookId(wordsFilter, true, currentBook.getPath());
                    break;

                case BY_WORD_COLLECTION:

                    if (wordsFilter == null)
                        return lw;
                    lw = ws.getAllByWordNameCollection(wordsFilter);
                    break;

                case NONE:
                    lw = ws.getAll();
                    break;

                default:
                    return lw;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lw;
    }

    public PreparedQuery<Word> getWordPreparedQuery(@Nullable Iterable<String> wordsFilter, @Nullable String bookIdFilter) {

        try {
            WordDaoService ws = mDatabaseManager.getWordService();

            switch (currentFilter) {
                case BY_BOOK:
                    if (bookIdFilter == null && currentBook == null)
                        throw new RuntimeException("Current book filter has not been set: \n" +
                                "'dataManager.setFilter( ? )' or argument 'bookIdFilter ?'");
                    return ws.getWordsByBookIdPreparedQuery(bookIdFilter == null ? currentBook.getPath() : bookIdFilter);
                default:
                    return ws.getAllWordsPreparedQuery();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Dao<Word, String> getWordDao() {
        try {
            WordDaoService ws = mDatabaseManager.getWordService();
            return ws.getWordDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Word getCurrentBooksWordByPage(String word) {
        try {
            WordDaoService ws = mDatabaseManager.getWordService();
            return ws.getWordByBookIdAndPage(word, currentBook.getPath(), currentPage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Word getRandomWord() {
        try {
            WordDaoService ws = mDatabaseManager.getWordService();
            Word word = ws.getRandomWord();
            Log.i(LOG_TAG, "random word '" + word + "' has been fetched.");
            return word;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteWords(WordFilter filter) {
        if (currentBook == null) return false;
        if (filter == WordFilter.BY_BOOK) {
            try {
                WordDaoService ws = mDatabaseManager.getWordService();
                List<Word> words = ws.getAllByBookId(currentBook.getPath());
                boolean succeed = ws.deleteAll(words);
                Log.i(LOG_TAG, "All words by book '" + currentBook.getName() + "' have been deleted from database: " + succeed);
                return succeed;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (filter == WordFilter.BY_BOOK) {
            throw new NotImplementedException("Selection by filter '" + filter + "' not implemented yet.");
        }
        Log.i(LOG_TAG, "Words have not been correctly deleted.");
        return false;
    }
}
