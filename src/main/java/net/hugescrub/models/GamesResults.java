package net.hugescrub.models;

import java.util.List;

public class GamesResults {

    private List<String> gameLinks;
    private List<String> gameNames;

    public List<String> getGameLinks() {
        return gameLinks;
    }

    public void setGameLinks(List<String> gameLinks) {
        this.gameLinks = gameLinks;
    }

    public List<String> getGameNames() {
        return gameNames;
    }

    public void setGameNames(List<String> gameNames) {
        this.gameNames = gameNames;
    }
}
