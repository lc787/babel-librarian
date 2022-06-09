package com.babel.entities;

import java.util.List;

public class BookInformation {
    private String title;
    private List<String> authors;
    private String publisher;
    private int year = 0;
    private String edition;
    private String series;
    private String volumeinfo;

    private String genre;

    public BookInformation() {

    }

    public BookInformation(String title, List<String> authors, String publisher, List<String> isbn, int year, String series, String edition, String volume, String volumeinfo, String genre) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.year = year;
        this.series = series;
        this.edition = edition;
        this.volumeinfo = volumeinfo;
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "BookInformation{" +
                "title='" + title + '\'' +
                ", author=" + authors +
                ", publisher='" + publisher + '\'' +
                ", year='" + year + '\'' +
                ", series='" + series + '\'' +
                ", edition='" + edition + '\'' +
                ", volume='" + volumeinfo + '\'' +
                '}';
    }
}
