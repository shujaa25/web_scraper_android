package com.ishujaa.scraper;

public class Target {
    private int id;
    private String name;
    private String url;
    private String primarySelector;
    private String secondarySelector;
    private String groupSelector;
    private long sleepDuration;
    private String currentData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentData() {
        return currentData;
    }

    public void setCurrentData(String currentData) {
        this.currentData = currentData;
    }

    public String getGroupSelector() {
        return groupSelector;
    }

    public void setGroupSelector(String groupSelector) {
        this.groupSelector = groupSelector;
    }

    public String getPrimarySelector() {
        return primarySelector;
    }

    public void setPrimarySelector(String primarySelector) {
        this.primarySelector = primarySelector;
    }

    public String getSecondarySelector() {
        return secondarySelector;
    }

    public void setSecondarySelector(String secondarySelector) {
        this.secondarySelector = secondarySelector;
    }

    public long getSleepDuration() {
        return sleepDuration;
    }

    public void setSleepDuration(long sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
