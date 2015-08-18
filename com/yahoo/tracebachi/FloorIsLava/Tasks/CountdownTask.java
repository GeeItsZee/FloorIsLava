package com.yahoo.tracebachi.FloorIsLava.Tasks;

import com.yahoo.tracebachi.FloorIsLava.Floor.Floor;
import org.bukkit.Bukkit;

/**
 * Created by tracebachi@yahoo.com (BigBossZee) on 12/21/2014.
 */
public class CountdownTask implements Runnable
{
    // Class members
    private Floor targetFloor;

    // Methods
    public CountdownTask(Floor floor)
    {
        targetFloor = floor;
    }

    @Override
    public void run() {
        Bukkit.getLogger().info( "FIL_LOG " + "runCountdownTask()" ); targetFloor.countdownTick(); }
}
