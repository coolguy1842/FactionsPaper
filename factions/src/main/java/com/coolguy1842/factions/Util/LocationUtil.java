package com.coolguy1842.factions.Util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
    public static String serializeLocation(Location location) {
        return String.format(
            "%f,%f,%f,%f,%f,%s",
            location.getX(), location.getY(), location.getZ(),
            location.getYaw(), location.getPitch(),
            location.getWorld().getUID()
        );
    }

    public static Location deserializeLocation(String location) {
        String[] splitLocation = location.split(",");

        Double x = Double.parseDouble(splitLocation[0]);
        Double y = Double.parseDouble(splitLocation[1]);
        Double z = Double.parseDouble(splitLocation[2]);
        
        Float yaw = Float.parseFloat(splitLocation[3]);
        Float pitch = Float.parseFloat(splitLocation[4]);
        
        UUID worldID = UUID.fromString(splitLocation[5]);

        return new Location(Bukkit.getWorld(worldID), x, y, z, yaw, pitch);
    }
}
