package com.trivectadigital.ziprydeuserapp.assist;

public class MessageReceivedEvent {

    public String message;
    public String title;

    public MessageReceivedEvent(String message) {
        this.message = message;
    }

    public MessageReceivedEvent(String message, String title) {
        this.message = message;
        this.title = title;
    }
}
