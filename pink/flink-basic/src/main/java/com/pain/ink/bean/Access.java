package com.pain.ink.bean;

public class Access {
    public String time;
    public String domain;
    public int traffic;

    public Access() {
    }

    public Access(String time, String domain, int traffic) {
        this.time = time;
        this.domain = domain;
        this.traffic = traffic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getTraffic() {
        return traffic;
    }

    public void setTraffic(int traffic) {
        this.traffic = traffic;
    }

    @Override
    public String toString() {
        return "Access{" +
                "time='" + time + '\'' +
                ", domain='" + domain + '\'' +
                ", traffic='" + traffic + '\'' +
                '}';
    }
}
