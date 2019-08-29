package com.example.pubgbattle;

public class UserProfile {
    public String name, pubgUsername, email, mobileNo, password, promoCode, dob, gender, profilePic, uid, winner, myReferCode;
    public int matchPlayed, totalKills, totalWinnings, walletCoins;

    public UserProfile() {
    }

    public UserProfile(String name, String pubgUsername, String dob, String gender) {
        this.name = name;
        this.pubgUsername = pubgUsername;
        this.dob = dob;
        this.gender = gender;
    }



    public UserProfile(String profilePic){
        this.profilePic = profilePic;
    }


    public UserProfile(String name, String pubgUsername, String email, String mobileNo, String password,
                       String dob, String gender, String promoCode, String profilePic,
                       int matchPlayed, int totalKills, int totalWinnings, int walletCoins, String uid, String winner, String myReferCode) {
        this.name = name;
        this.pubgUsername = pubgUsername;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.promoCode = promoCode;
        this.dob = dob;
        this.gender = gender;
        this.profilePic = profilePic;
        this.matchPlayed = matchPlayed;
        this.totalKills = totalKills;
        this.totalWinnings = totalWinnings;
        this.walletCoins = walletCoins;
        this.uid = uid;
        this.winner = winner;
        this.myReferCode = myReferCode;
    }

    public String getMyReferCode() {
        return myReferCode;
    }

    public void setMyReferCode(String myReferCode) {
        this.myReferCode = myReferCode;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPubgUsername() {
        return pubgUsername;
    }

    public void setPubgUsername(String pubgUsername) {
        this.pubgUsername = pubgUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getMatchPlayed() {
        return matchPlayed;
    }

    public void setMatchPlayed(int matchPlayed) {
        this.matchPlayed = matchPlayed;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }

    public int getTotalWinnings() {
        return totalWinnings;
    }

    public void setTotalWinnings(int totalWinnings) {
        this.totalWinnings = totalWinnings;
    }

    public int getWalletCoins() {
        return walletCoins;
    }

    public void setWalletCoins(int walletCoins) {
        this.walletCoins = walletCoins;
    }
}
