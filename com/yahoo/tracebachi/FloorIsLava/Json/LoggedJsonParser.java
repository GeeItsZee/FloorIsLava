package com.yahoo.tracebachi.FloorIsLava.Json;

import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonElement;
import net.minecraft.util.com.google.gson.JsonObject;
import org.bukkit.Bukkit;

/**
 * Created by tracebachi@yahoo.com (BigBossZee) on 12/20/2014.
 */
public class LoggedJsonParser
{
    // Methods
    public static boolean getBoolean( String tag, JsonObject obj,
                                      String field, boolean defVal )
    {
        // Get
        JsonElement elem = obj.get(field);

        // Check
        if( elem != null )
        {
            return elem.getAsBoolean();
        }
        else
        {
            Bukkit.getLogger().severe( "JSON Error: (" + tag + ","
                    + field + ") was not found." );
            return defVal;
        }
    }

    public static int getInt( String tag, JsonObject obj,
                              String field, int defVal )
    {
        // Get
        JsonElement elem = obj.get(field);

        // Check
        if( elem != null )
        {
            return elem.getAsInt();
        }
        else
        {
            Bukkit.getLogger().severe( "JSON Error: (" + tag + ","
                    + field + ") was not found." );
            return defVal;
        }
    }

    public static String getString( String tag, JsonObject obj,
                              String field, String defVal )
    {
        // Get
        JsonElement elem = obj.get(field);

        // Check
        if( elem != null )
        {
            return elem.getAsString();
        }
        else
        {
            Bukkit.getLogger().severe( "JSON Error: (" + tag + ","
                    + field + ") was not found." );
            return defVal;
        }
    }

    public static String[] getStringArray( String tag, JsonObject obj,
                                    String field )
    {
        // Get
        JsonElement elem = obj.get(field);

        // Check
        if( elem != null )
        {
            JsonArray array = elem.getAsJsonArray();
            String[] stringArray = new String[ array.size() ];
            for( int i = 0; i < stringArray.length; ++i )
            {
                stringArray[i] = array.get(i).getAsString();
            }
            return stringArray;
        }
        else
        {
            Bukkit.getLogger().severe( "JSON Error: (" + tag + ","
                    + field + ") was not found." );
            return null;
        }
    }
}
