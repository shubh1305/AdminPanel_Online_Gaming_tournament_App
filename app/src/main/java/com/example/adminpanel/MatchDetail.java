package com.example.adminpanel;

public class MatchDetail {
    private String MatchTitle, MatchDate, MatchTime, MatchType, MatchVersion, MatchMap, MatchStatus, MatchKey, MatchPic, matchIdShow, matchWatchLink;
    private int MatchWinningPrize, MatchPerKill, MatchEntryFee, MatchTotalSpot, MatchOccupiedSpot;

    public MatchDetail() {
    }


    public MatchDetail(String MatchKey, String MatchTitle, String MatchDate, String MatchTime, String MatchType,
                       String MatchVersion, String MatchMap, String MatchStatus, int MatchWinningPrize,
                       int MatchPerKill, int MatchEntryFee, int MatchTotalSpot, int MatchOccupiedSpot, String MatchPic,
                       String matchIdShow, String matchWatchLink) {
     this.MatchKey = MatchKey;
     this.MatchTitle = MatchTitle;
     this.MatchDate = MatchDate;
     this.MatchTime = MatchTime;
     this.MatchType = MatchType;
     this.MatchVersion = MatchVersion;
     this.MatchMap = MatchMap;
     this.MatchStatus = MatchStatus;
     this.MatchWinningPrize = MatchWinningPrize;
     this.MatchPerKill = MatchPerKill;
     this.MatchEntryFee = MatchEntryFee;
     this.MatchTotalSpot = MatchTotalSpot;
     this.MatchOccupiedSpot = MatchOccupiedSpot;
     this.MatchPic = MatchPic;
     this.matchIdShow = matchIdShow;
     this.matchWatchLink = matchWatchLink;
    }

    public String getMatchIdShow() {
        return matchIdShow;
    }

    public void setMatchIdShow(String matchIdShow) {
        this.matchIdShow = matchIdShow;
    }

    public String getMatchWatchLink() {
        return matchWatchLink;
    }

    public void setMatchWatchLink(String matchWatchLink) {
        this.matchWatchLink = matchWatchLink;
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

    public String getMatchKey() {
        return MatchKey;
    }

    public void setMatchKey(String matchKey) {
        MatchKey = matchKey;
    }

    public String getMatchPic() {
        return MatchPic;
    }

    public void setMatchPic(String matchPic) {
        MatchPic = matchPic;
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
