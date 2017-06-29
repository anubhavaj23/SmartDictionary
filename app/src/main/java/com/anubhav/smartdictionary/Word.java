package com.anubhav.smartdictionary;

/**
 * Created by anubh on 19-Apr-17.
 */

public class Word {
    String word,meaning;

    public Word(String word, String meaning){
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }
}
