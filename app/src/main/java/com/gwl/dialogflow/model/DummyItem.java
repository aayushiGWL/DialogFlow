package com.gwl.dialogflow.model;

/**
 * A dummy item representing a piece of content.
 */
public class DummyItem {
    public final String id;
    public final String content;
    public final String message_type;

    public DummyItem(String id, String content, String message_type) {
        this.id = id;
        this.content = content;
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
