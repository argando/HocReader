package com.eaccid.bookreader.db.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "books")
public class Book implements Serializable{

    @DatabaseField(columnName = "id", canBeNull = false, id = true)
    private String path;

    @DatabaseField
    private String name;

    @DatabaseField
    private int pages;

    @ForeignCollectionField(foreignFieldName = "book")
    private ForeignCollection<Word> words;

    public Book() {

    }

    public Book(String path, String name, int pages) {
        this.path = path;
        this.name = name;
        this.pages = pages;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id = '" + path +'\'' +
                ", name = '" + name + '\'' +
                ", pages = " + pages +
                '}';
    }
}