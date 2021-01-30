package com.example.MA02_20170953;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class BoxDto implements Serializable {

    String fcltyNm; // 시설명
    double latitude; // 위도
    double longitude; // 경도
    String location; // 주소
    String weekdayOperOpenHhmm; // 평일운영시작시간
    String weekdayOperColseHhmm; // 평일운영종료시간
    String satOperOperOpenHhmm; // 토요일운영시작시간
    String satOperCloseHhmm; // 토요일운영종료시간
    String holidayOperOpenHhmm; // 공휴일운영시작시간
    String holidayCloseOpenHhmm; // 공휴일운영종료시간
    String freeUseTime; // 무료이용시간

    public String getFcltyNm() {
        return fcltyNm;
    }

    public void setFcltyNm(String fcltyNm) {
        this.fcltyNm = fcltyNm;
    }

    public String getWeekdayOperOpenHhmm() {
        return weekdayOperOpenHhmm;
    }

    public void setWeekdayOperOpenHhmm(String weekdayOperOpenHhmm) {
        this.weekdayOperOpenHhmm = weekdayOperOpenHhmm;
    }

    public String getWeekdayOperColseHhmm() {
        return weekdayOperColseHhmm;
    }

    public void setWeekdayOperColseHhmm(String weekdayOperColseHhmm) {
        this.weekdayOperColseHhmm = weekdayOperColseHhmm;
    }

    public String getSatOperOperOpenHhmm() {
        return satOperOperOpenHhmm;
    }

    public void setSatOperOperOpenHhmm(String satOperOperOpenHhmm) {
        this.satOperOperOpenHhmm = satOperOperOpenHhmm;
    }

    public String getSatOperCloseHhmm() {
        return satOperCloseHhmm;
    }

    public void setSatOperCloseHhmm(String satOperCloseHhmm) {
        this.satOperCloseHhmm = satOperCloseHhmm;
    }

    public String getHolidayOperOpenHhmm() {
        return holidayOperOpenHhmm;
    }

    public void setHolidayOperOpenHhmm(String holidayOperOpenHhmm) {
        this.holidayOperOpenHhmm = holidayOperOpenHhmm;
    }

    public String getHolidayCloseOpenHhmm() {
        return holidayCloseOpenHhmm;
    }

    public void setHolidayCloseOpenHhmm(String holidayCloseOpenHhmm) {
        this.holidayCloseOpenHhmm = holidayCloseOpenHhmm;
    }

    public String getFreeUseTime() {
        return freeUseTime;
    }

    public void setFreeUseTime(String freeUseTime) {
        this.freeUseTime = freeUseTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @NonNull
    @Override
    public String toString() {
        return "";
    }
}
