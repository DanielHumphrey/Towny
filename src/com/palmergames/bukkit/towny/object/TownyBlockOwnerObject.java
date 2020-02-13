package com.palmergames.bukkit.towny.object;

import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Allows objects to contain townblocks to be accessed/manipulated. 
 * 
 * @author EdgarL
 * @author Shade
 * @author Suneet Tipirneni (Siris)
 */
public interface TownBlockOwner extends Permissible {

	/**
	 * Sets the townblocks
	 * 
	 * @param townBlocks the townblocks to set.
	 */
	void setTownblocks(List<TownBlock> townBlocks);

	/**
	 * Gets the townblocks.
	 * 
	 * @return The townblocks this object contains.
	 */
	List<TownBlock> getTownBlocks();

	/**
	 * Checks whether object has townblock or not.
	 * 
	 * @param townBlock The townblock to check for.
	 * @return A boolean indicating if it was found or not.
	 */
	boolean hasTownBlock(TownBlock townBlock);

	/**
	 * Adds a townblock to the list of existing townblocks.
	 * 
	 * @param townBlock The townblock to add.
	 * @throws AlreadyRegisteredException When the townblock is already in the list.
	 */
	void addTownBlock(TownBlock townBlock) throws AlreadyRegisteredException;

	/**
	 * Removes townblock from the list of existing townblocks.
	 * 
	 * @param townBlock The townblock to remove.
	 * @throws NotRegisteredException Thrown when the townblock given is not in the list.
	 */
	void removeTownBlock(TownBlock townBlock) throws NotRegisteredException;
}