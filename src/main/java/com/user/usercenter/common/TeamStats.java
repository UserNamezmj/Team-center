package com.user.usercenter.common;

public enum TeamStats {

    TEAM_PUBLICITY(0, "公开"),
    TEAM_PRIVACY(1, "私密"),
    TEAM_ENCIPHER(2, "加密");
    private int code;
    private String des;

    TeamStats(int code, String des) {
        this.code = code;
        this.des = des;
    }


    public static TeamStats getEnumByValue(Integer code) {
        if (code == null) {
            return null;
        }
        TeamStats[] values = TeamStats.values();
        for (TeamStats teamStats : values) {
            if (teamStats.getCode() == code) {
                return teamStats;
            }
        }
        return null;


    }

    public int getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }

}
