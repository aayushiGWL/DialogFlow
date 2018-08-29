package com.gwl.dialogflow.model;

public class ChatModel {

    private String id;
    private String content;
    private String message_type;


    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getMessage_type() {
        return message_type;
    }


    @Override
    public String toString() {
        return content;
    }
}
