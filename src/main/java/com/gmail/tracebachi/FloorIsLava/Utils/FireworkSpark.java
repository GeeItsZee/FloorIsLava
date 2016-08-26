/*
 * This file is part of FloorIsLava.
 *
 * FloorIsLava is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FloorIsLava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FloorIsLava.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.tracebachi.FloorIsLava.Utils;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Jeremy Lugo on 8/17/2016.
 */
public class FireworkSpark
{
    private static final Class<?> entityFireworks = getClass(
        "net.minecraft.server.",
        "EntityFireworks");
    private static final Class<?> craftFirework = getClass(
        "org.bukkit.craftbukkit.",
        "entity.CraftFirework");

    public static void spark(FireworkEffect effect, Location location)
    {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);

        try
        {
            Object fireworkObject = craftFirework.cast(firework);
            Method handle = fireworkObject.getClass().getMethod("getHandle");
            Object entityFirework = handle.invoke(fireworkObject);
            Field expectedLifespan = entityFireworks.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworks.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException |
            IllegalArgumentException | InvocationTargetException | NoSuchFieldException |
            NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    private static Class<?> getClass(String prefix, String nmsClassString)
    {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";

        try
        {
            return Class.forName(prefix + version + nmsClassString);
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
