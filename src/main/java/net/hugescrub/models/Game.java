package net.hugescrub.models;

import java.net.URL;

public interface Game {

    String getTitle();

    void setTitle(String title);

    URL getUrl();

    void setUrl(URL url);

    String getSize();

    void setSize(String size);

    String getGenre();

    void setGenre(String genre);
}
