package com.knepe.megamemory.models.helpers;

/**
 * Created by knepe on 2013-08-20.
 */
public class ThemeHelper {
    public static String getPath(int theme){
        String basePath = "gfx/themes/";
        switch(theme){
            case 1:
                return basePath + "animals";
            case 2:
                return basePath + "fruits";
            case 3:
                return basePath + "shapes";
            case 4:
                return basePath + "vehicles";
            case 5:
                return basePath + "numbers";
            default:
                return "";
        }
    }
}
