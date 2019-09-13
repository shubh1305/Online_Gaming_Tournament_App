package com.example.pubgbattle;

public class MyStats {

    String title, date;
    int kills, winning;

    public MyStats() {
    }

    public MyStats(String title, String date, int kills, int winning) {
        this.title = title;
        this.date = date;
        this.kills = kills;
        this.winning = winning;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getWinning() {
        return winning;
    }

    public void setWinning(int winning) {
        this.winning = winning;
    }
}

