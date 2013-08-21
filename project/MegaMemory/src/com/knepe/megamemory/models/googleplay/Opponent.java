package com.knepe.megamemory.models.googleplay;

/**
 * Created by knepe on 2013-08-22.
 */
public class Opponent {
    public Opponent(){}

    public Opponent(String id, String displayName){
        this.id = id;
        this.displayName = displayName;
    }

    private String id;
    private String displayName;

    public String getId(){
        return id;
    }

    public String getDisplayName(){
        return displayName;
    }
}
