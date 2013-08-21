package com.knepe.megamemory.models.helpers;

/**
 * Created by knepe on 2013-08-20.
 */
public class ThemeHelper {
    public static String getPath(int theme){
        String basePath = "gfx/themes/";
        switch(theme){
            case 0:
                return basePath + "animals";
            case 1:
                return basePath + "fruits";
            case 2:
                return basePath + "shapes";
            case 3:
                return basePath + "vehicles";
            case 4:
                return basePath + "makeup";
            case 5:
                return basePath + "numbers";
            default:
                return "";
        }
    }
}
