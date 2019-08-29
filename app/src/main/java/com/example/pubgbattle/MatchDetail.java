package com.example.pubgbattle;

public class MatchDetail {
    private String MatchTitle, MatchDate, MatchTime, MatchType, MatchVersion, MatchMap, MatchStatus, MatchKey, MatchWatchLink, MatchPic,
    MatchIdShow;
    private int MatchWinningPrize, MatchPerKill, MatchEntryFee, MatchTotalSpot, MatchOccupiedSpot;

    public MatchDetail() {
    }


    public MatchDetail(String matchKey, String matchTitle, String matchDate, String matchTime, String matchType,
                       String matchVersion, String matchMap, String matchStatus, int matchWinningPrize,
                       int matchPerKill, int matchEntryFee, int matchTotalSpot, int matchOccupiedSpot, String matchIdShow,
                       String matchWatchLink, String matchPic) {
        this.MatchKey = matchKey;
        this.MatchTitle = matchTitle;
        this.MatchDate = matchDate;
        this.MatchTime = matchTime;
        this.MatchType = matchType;
        this.MatchVersion = matchVersion;
        this.MatchMap = matchMap;
        this.MatchStatus = matchStatus;
        this.MatchWinningPrize = matchWinningPrize;
        this.MatchPerKill = matchPerKill;
        this.MatchEntryFee = matchEntryFee;
        this.MatchTotalSpot = matchTotalSpot;
        this.MatchOccupiedSpot = matchOccupiedSpot;
        this.MatchIdShow = matchIdShow;
        this.MatchWatchLink = matchWatchLink;
        this.MatchPic = matchPic;
    }

    public String getMatchPic() {
        return MatchPic;
    }

    public void setMatchPic(String matchPic) {
        MatchPic = matchPic;
    }

    public String getMatchWatchLink() {
        return MatchWatchLink;
    }

    public void setMatchWatchLink(String matchWatchLink) {
        MatchWatchLink = matchWatchLink;
    }

    public String getMatchIdShow() {
        return MatchIdShow;
    }

    public void setMatchIdShow(String matchIdShow) {
        MatchIdShow = matchIdShow;
    }

    public String getMatchKey() {
        return MatchKey;
    }

    public void setMatchKey(String matchKey) {
        MatchKey = matchKey;
    }

    public String getMatchTitle() {
        return MatchTitle;
    }

    public void setMatchTitle(String matchTitle) {
        MatchTitle = matchTitle;
    }

    public String getMatchDate() {
        return MatchDate;
    }

    public void setMatchDate(String matchDate) {
        MatchDate = matchDate;
    }

    public String getMatchTime() {
        return MatchTime;
    }

    public void setMatchTime(String matchTime) {
        MatchTime = matchTime;
    }

    public String getMatchType() {
        return MatchType;
    }

    public void setMatchType(String matchType) {
        MatchType = matchType;
    }

    public String getMatchVersion() {
        return MatchVersion;
    }

    public void setMatchVersion(String matchVersion) {
        MatchVersion = matchVersion;
    }

    public String getMatchMap() {
        return MatchMap;
    }

    public void setMatchMap(String matchMap) {
        MatchMap = matchMap;
    }

    public String getMatchStatus() {
        return MatchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        MatchStatus = matchStatus;
    }

    public int getMatchWinningPrize() {
        return MatchWinningPrize;
    }

    public void setMatchWinningPrize(int matchWinningPrize) {
        MatchWinningPrize = matchWinningPrize;
    }

    public int getMatchPerKill() {
        return MatchPerKill;
    }

    public void setMatchPerKill(int matchPerKill) {
        MatchPerKill = matchPerKill;
    }

    public int getMatchEntryFee() {
        return MatchEntryFee;
    }

    public void setMatchEntryFee(int matchEntryFee) {
        MatchEntryFee = matchEntryFee;
    }

    public int getMatchTotalSpot() {
        return MatchTotalSpot;
    }

    public void setMatchTotalSpot(int matchTotalSpot) {
        MatchTotalSpot = matchTotalSpot;
    }

    public int getMatchOccupiedSpot() {
        return MatchOccupiedSpot;
    }

    public void setMatchOccupiedSpot(int matchOccupiedSpot) {
        MatchOccupiedSpot = matchOccupiedSpot;
    }
}

