package com.yahoo.tracebachi.FloorIsLava.Tasks;

import com.yahoo.tracebachi.FloorIsLava.Floor.Floor;
import org.bukkit.Bukkit;

/**
 * Created by tracebachi@yahoo.com (BigBossZee) on 12/21/2014.
 */
public class PlayTickTask implements Runnable
{
    // Class members
    private Floor targetFloor;

    // Methods
    public PlayTickTask(Floor floor)
    {
        targetFloor = floor;
    }

    @Override
    public void run() {
        Bukkit.getLogger().info( "FIL_LOG " + "runTickTas()" ); targetFloor.playTick(); }
}
