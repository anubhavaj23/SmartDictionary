package com.anubhav.smartdictionary;

/**
 * Created by anubh on 25-Mar-17.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Stringcheck {
    private static final String WORD = "^[A-Za-z]+$";
    private Pattern word;
    private Matcher matcher;
    public Stringcheck(){
        word = Pattern.compile(WORD);
    }
    public boolean isoneword(final String text){
        matcher = word.matcher(text);
        return matcher.matches();
    }
}

