package net.hugescrub.models;

import java.net.URL;

public class GameImpl implements Game {

    private String title;
    private String size;
    private URL url;
    private String genre;

    public GameImpl(String title, String size, URL url, String genre) {
        this.title = title;
        this.size = size;
        this.url = url;
        this.genre = genre;
    }



    @Override
    public String toString() {
        return "Game{" +
                "title='" + title + '\'' +
                ", size='" + size + '\'' +
                ", URL='" + url + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public String getSize() {
        return this.size;
    }

    @Override
    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String getGenre() {
        return this.genre;
    }

    @Override
    public void setGenre(String genre) {
        this.genre = genre;
    }
}
