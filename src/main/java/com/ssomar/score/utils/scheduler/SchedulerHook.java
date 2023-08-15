package com.ssomar.score.utils.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface SchedulerHook {

    ScheduledTask runTask(Runnable runnable, long delay);

    ScheduledTask runRepeatingTask(Runnable runnable, long initDelay, long period);

    ScheduledTask runEntityTask(Runnable runnable, Runnable retired, Entity entity, long delay);


    ScheduledTask runLocationTask(Runnable runnable, Runnable retired, Location location, long delay);

    ScheduledTask runEntityTaskAsap(Runnable runnable, Runnable retired, Entity entity);

}