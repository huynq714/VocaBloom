package com.vocabloom.app.model;
public class Word {
    public final String id, topicId, english, phonetic, meaning, example;
    public Word(String id, String topicId, String english, String phonetic, String meaning, String example) {
        this.id=id; this.topicId=topicId; this.english=english; this.phonetic=phonetic; this.meaning=meaning; this.example=example;
    }
}
