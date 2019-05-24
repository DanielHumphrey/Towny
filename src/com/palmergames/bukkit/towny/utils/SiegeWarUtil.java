package com.palmergames.bukkit.towny.utils;

import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.war.eventwar.War;
import com.palmergames.bukkit.towny.war.siegewar.Siege;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Anonymoose on 19/05/2019.
 */
public class SiegeWarUtil {

    public static boolean isPlayerWithinMaxWarZoneDistanceFromHomeBlock(TownBlock townBlockWherePlayerIs,
                                                                        Town town) throws TownyException {
        if(!town.hasHomeBlock())
            return false;

        TownBlock homeBlock = town.getHomeBlock();

        int warZoneRadiusTownBlocks = TownySettings.getWarSiegeMaxWarzoneDistanceFromHomeblock();

        //Player is too far north
        if (townBlockWherePlayerIs.getZ() < homeBlock.getZ() - warZoneRadiusTownBlocks)
            return false;

        //Player is too far south
        if (townBlockWherePlayerIs.getZ() > homeBlock.getZ() + warZoneRadiusTownBlocks)
            return false;

        //Player is too far east
        if (townBlockWherePlayerIs.getX() > homeBlock.getX() + warZoneRadiusTownBlocks)
            return false;

        //Player is too far west
        if (townBlockWherePlayerIs.getX() < homeBlock.getX() - warZoneRadiusTownBlocks)
            return false;

        return true; //Player is in the warzone
    }

    public static boolean isGivenTownBlockOnTheTownBorder(TownBlock townBlock) {
        WorldCoord worldCoord = townBlock.getWorldCoord();

        int[][] offset = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        for (int i = 0; i < 4; i++)
            try {
                TownBlock edgeTownBlock = worldCoord.getTownyWorld().getTownBlock(new Coord(worldCoord.getX() + offset[i][0], worldCoord.getZ() + offset[i][1]));
                boolean sameTown = edgeTownBlock.getTown() == townBlock.getTown();
                if (!sameTown)
                    return true; //If the adjacent plot is in a different town, return true
            } catch (NotRegisteredException e) {
                return true;  //If the adjacent plot is not in a town, return true
            }
        return false;
    }

    public static boolean doesPlayerHaveANonAirBlockAboveThem(Player player) {
        Location loc = player.getEyeLocation().add(0,1,0);

        while(loc.getY() < 256)
        {
            if(loc.getBlock().getType() != Material.AIR)
            {
                return true;   //There is a non-air block above them
            }
            loc.add(0,1,0);
        }
        return false;  //There is nothing but air above them
    }
}
