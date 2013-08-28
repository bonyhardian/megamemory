package com.knepe.megamemory.models.googleplay;

public class Opponent {
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
