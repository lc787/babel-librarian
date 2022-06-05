package com.babel.entities;

import java.util.List;

public class BookInformation {
    private String title;
    private String author;
    private String publisher;
    private String year;
    private String edition;
    private String series;
    private String volumeinfo;

    public BookInformation() {

    }

    public BookInformation(String title, String author, String publisher, List<String> isbn, String year, String series, String edition, String volume, String volumeinfo) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.series = series;
        this.edition = edition;
        this.volumeinfo = volumeinfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getVolumeInfo() {
        return volumeinfo;
    }

    public void setVolumeInfo(String volumeInfo) {
        this.volumeinfo = volumeInfo;
    }

    @Override
    public String toString() {
        return "BookInformation{" +
                "title='" + title + '\'' +
                ", author=" + author +
                ", publisher='" + publisher + '\'' +
                ", year='" + year + '\'' +
                ", series='" + series + '\'' +
                ", edition='" + edition + '\'' +
                ", volume='" + volumeinfo + '\'' +
                '}';
    }
}
