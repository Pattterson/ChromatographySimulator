package com.chromasim.chromatographyhome;

import javafx.scene.control.ComboBox;

public class IntegrationEvent {

    private ComboBox<String> eventType = new ComboBox<>();
    private String eventStartTime;
    private String eventEndTime;
    private String eventValue;

    public IntegrationEvent( String eventStartTime, String eventEndTime) {
        eventType.getItems().add("Inhibit Integration");
        eventType.getItems().add("Set Threshold");
        eventType.getItems().add("Set Peak Width");
        eventType.getItems().add("Baseline continue");
        eventType.getItems().add("Valley to Valley");

        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;

        eventValue=Integer.toString(3);
    }
    public IntegrationEvent(String eventTypeString, String eventStartTime,String eventEndTime){
        this(eventStartTime,eventEndTime);
        this.eventType.getSelectionModel().select(eventTypeString);

        eventValue=Integer.toString(3);
    }

    public String getEventValue() {
        return eventValue;
    }

    public void setEventValue(String eventValue) {
        this.eventValue = eventValue;
    }

    public ComboBox getEventType() {
        return eventType;
    }

    public void setEventType(ComboBox eventType) {
        this.eventType = eventType;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }
}
