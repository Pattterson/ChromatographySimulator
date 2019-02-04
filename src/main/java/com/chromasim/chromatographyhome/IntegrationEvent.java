package com.chromasim.chromatographyhome;

import javafx.scene.control.ComboBox;

public class IntegrationEvent {

    private ComboBox<String> eventType = new ComboBox<>();
    private Double eventStartTime;
    private Double eventEndTime;

    public IntegrationEvent( Double eventStartTime, Double eventEndTime) {
        eventType.getItems().add("Inhibit Integration");
        eventType.getItems().add("Set Threshold");
        eventType.getItems().add("Set Peak Width");
        eventType.getItems().add("Baseline continue");
        eventType.getItems().add("Valley to Valley");

        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }
    public IntegrationEvent(String eventTypeString, Double eventStartTime,Double eventEndTime){
        this(eventStartTime,eventEndTime);
        this.eventType.getSelectionModel().select(eventTypeString);
    }


    public ComboBox getEventType() {
        return eventType;
    }

    public void setEventType(ComboBox eventType) {
        this.eventType = eventType;
    }

    public Double getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(Double eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public Double getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(Double eventEndTime) {
        this.eventEndTime = eventEndTime;
    }
}
