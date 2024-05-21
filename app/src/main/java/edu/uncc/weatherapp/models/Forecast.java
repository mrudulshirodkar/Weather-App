package edu.uncc.weatherapp.models;

public class Forecast {
    String startTime,temperature,shortforcast,windspeed,icon,value, humidity;

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getShortforcast() {
        return shortforcast;
    }

    public void setShortforcast(String shortforcast) {
        this.shortforcast = shortforcast;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(String windspeed) {
        this.windspeed = windspeed;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "startTime='" + startTime + '\'' +
                ", temperature='" + temperature + '\'' +
                ", shortforcast='" + shortforcast + '\'' +
                ", windspeed='" + windspeed + '\'' +
                ", icon='" + icon + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
