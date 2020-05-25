package com.palmergames.bukkit.towny.object;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.confirmations.ConfirmationHandler;
import com.palmergames.bukkit.towny.event.PlotChangeOwnerEvent;
import com.palmergames.bukkit.towny.event.PlotChangeTypeEvent;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import com.palmergames.bukkit.util.BukkitTools;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashSet;
import java.util.UUID;

public class TownBlock extends TownyObject {

	// TODO: Admin only or possibly a group check
	// private List<Group> groups;
	private TownyWorld world;
	private UUID townID = null;
	private UUID residentID = null;
	private TownBlockType type = TownBlockType.RESIDENTIAL;
	private int x, z;
	private double plotPrice = -1;
	private boolean locked = false;
	private boolean outpost = false;
	private transient HashSet<CustomDataField<?>> metadata = null;
	private transient PlotGroup plotGroup;

	//Plot level permissions
	protected TownyPermission permissions = new TownyPermission();
	
	public TownBlock(UUID uniqueIdentifier) {
		super(uniqueIdentifier);
	}
	
	public TownBlock(UUID uniqueIdentifier, int x, int z, TownyWorld world) {
		super(uniqueIdentifier);
		this.x = x;
		this.z = z;
		this.setWorld(world);
		this.setName("");
	}
	
	public void setTownID(UUID townID) {
		this.townID = townID;
	}

	public void setTown(@Nullable Town town) {
		
		if (town == null) {
			setTownID(null);
			return;
		}
		
		setTownID(town.getUniqueIdentifier());
	}

	public Town getTown() throws NotRegisteredException {
		return TownyUniverse.getInstance().getTown(getTownID());
	}

	public boolean hasTown() {
		try {
			return TownyUniverse.getInstance().getTown(getTownID()) != null;
		} catch (NotRegisteredException e) {
			return false;
		}
	}

	public void setResident(Resident resident) {
		if (resident == null) {
			residentID = null;
			return;
		}
		
		try {
			if (hasResident())
				getResident().removeTownBlock(this);
		} catch (NotRegisteredException ignored) {}
		
		residentID = resident.getUniqueIdentifier();

		try {
			Bukkit.getPluginManager().callEvent(new PlotChangeOwnerEvent(getResident(), resident, this));
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
	}

	public Resident getResident() throws NotRegisteredException {
		
		if (residentID == null) {
			throw new NotRegisteredException(String.format("The TownBlock at (%s, %d, %d) is not registered to a resident.", world.getName(), x, z));
		}
		
		Resident resident = TownyUniverse.getInstance().getResident(residentID);
		if (resident == null)
			throw new NotRegisteredException(String.format("The TownBlock at (%s, %d, %d) is not registered to a resident.", world.getName(), x, z));
		return resident;
	}

	public boolean hasResident() {

		return residentID != null;
	}

	public boolean isOwner(TownBlockOwner owner) {

		try {
			if (owner == getTown())
				return true;
		} catch (NotRegisteredException ignored) {}

		try {
			if (owner == getResident())
				return true;
		} catch (NotRegisteredException ignored) {}

		return false;
	}

	public void setPlotPrice(double ForSale) {

		this.plotPrice = ForSale;

	}

	public double getPlotPrice() {

		return plotPrice;
	}

	public boolean isForSale() {

		return getPlotPrice() != -1.0;
	}

	public void setPermissions(String line) {

		//permissions.reset(); not needed, already done in permissions.loadString()
		permissions.load(line);
	}

	public TownyPermission getPermissions() {

		/*
		 * Return our perms
		 */
		return permissions;
	}

	/**
	 * @return the outpost
	 */
	public boolean isOutpost() {

		return outpost;
	}

	/**
	 * @param outpost the outpost to set
	 */
	public void setOutpost(boolean outpost) {

		this.outpost = outpost;
	}

	public TownBlockType getType() {

		return type;
	}
	
	public void setType(TownBlockType type) {
		if (type != this.type)
			this.permissions.reset();

		if (type != null){
			Bukkit.getPluginManager().callEvent(new PlotChangeTypeEvent(this.type, type, this));
		}
		this.type = type;

		// Custom plot settings here
		switch (type) {
		
		case RESIDENTIAL:

			case COMMERCIAL:

			case EMBASSY:

			case WILDS:

			case FARM:

			case BANK:

				//setPermissions("residentSwitch,allySwitch,outsiderSwitch");
			if (this.hasResident()) {
				try {
					setPermissions(getResident().getPermissions().toString());
				} catch (NotRegisteredException e) {
					e.printStackTrace();
				}
			} else {
				try {
					setPermissions(getTown().getPermissions().toString());
				} catch (NotRegisteredException e) {
					e.printStackTrace();
				}
			}
			
			break;

			case ARENA:
			
			setPermissions("pvp");
			break;

			case SPLEEF:

			case JAIL:

				setPermissions("denyAll");
			break;

		case INN:
			
			setPermissions("residentSwitch,allySwitch,outsiderSwitch");
			break;
		}
			
		
		// Set the changed status.
		this.setChanged(false);
				
	}

	public void setType(int typeId) {

		setType(TownBlockType.lookup(typeId));
	}

	public void setType(String typeName, Resident resident) throws TownyException, EconomyException {

		if (typeName.equalsIgnoreCase("reset"))
			typeName = "default";					
		
		TownBlockType type = TownBlockType.lookup(typeName);
		
		if (type == null)
			throw new TownyException(TownySettings.getLangString("msg_err_not_block_type"));

		double cost;
		switch (type) {
		case COMMERCIAL:
			cost = TownySettings.getPlotSetCommercialCost();
			break;
		case EMBASSY:
			cost = TownySettings.getPlotSetEmbassyCost();
			break;
		case ARENA:
			cost = TownySettings.getPlotSetArenaCost();
			break;
		case WILDS:
			cost = TownySettings.getPlotSetWildsCost();
			break;
		case INN:
			cost = TownySettings.getPlotSetInnCost();
			break;
		case JAIL:
			cost = TownySettings.getPlotSetJailCost();
			break;
		case FARM:
			cost = TownySettings.getPlotSetFarmCost();
			break;
		case BANK:
			cost = TownySettings.getPlotSetBankCost();
			break;
		default: 
			cost = 0;
		}
		
		// Test if we can pay first to throw an exception.
		if (cost > 0 && TownySettings.isUsingEconomy() && !resident.getAccount().canPayFromHoldings(cost))
			throw new EconomyException(String.format(TownySettings.getLangString("msg_err_cannot_afford_plot_set_type_cost"), type, TownyEconomyHandler.getFormattedBalance(cost)));

		// Handle payment via a confirmation to avoid suprise costs.
		if (cost > 0 && TownySettings.isUsingEconomy()) {
			Confirmation confirmation = new Confirmation(() -> {
		
				try {
					resident.getAccount().pay(cost, String.format("Plot set to %s", type));
				} catch (EconomyException ignored) {
				}					

				TownyMessaging.sendMessage(resident, String.format(TownySettings.getLangString("msg_plot_set_cost"), TownyEconomyHandler.getFormattedBalance(cost), type));
			
				if (this.isJail())
					try {
						this.getTown().removeJailSpawn(this.getCoord());
					} catch (NotRegisteredException ignored) {
					}
				
				setType(type);
			});
			confirmation.setTitle(String.format(TownySettings.getLangString("msg_confirm_purchase"), TownyEconomyHandler.getFormattedBalance(cost)));
			ConfirmationHandler.sendConfirmation(BukkitTools.getPlayerExact(resident.getName()), confirmation);
		// No payment required so just change the type.
		} else {
			if (this.isJail())
				this.getTown().removeJailSpawn(this.getCoord());
			setType(type);
		}
	}

	public boolean isHomeBlock() {

		try {
			return getTown().isHomeBlock(this);
		} catch (NotRegisteredException e) {
			return false;
		}
	}
	
	@Override
	public void setName(String newName) {
		super.setName(newName.replace("_", " ")); 
	}

	public void setX(int x) {

		this.x = x;
	}

	public int getX() {

		return x;
	}

	public void setZ(int z) {

		this.z = z;
	}

	public int getZ() {

		return z;
	}

	public Coord getCoord() {

		return new Coord(x, z);
	}

	public WorldCoord getWorldCoord() {

		return new WorldCoord(world.getName(), x, z);
	}

	/**
	 * Is the TownBlock locked
	 * 
	 * @return the locked
	 */
	public boolean isLocked() {

		return locked;
	}

	/**
	 * @param locked is the to locked to set
	 */
	public void setLocked(boolean locked) {

		this.locked = locked;
	}

	public void setWorld(TownyWorld world) {

		this.world = world;
	}

	public TownyWorld getWorld() {

		return world;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == this)
			return true;
		if (!(obj instanceof TownBlock))
			return false;

		TownBlock o = (TownBlock) obj;
		return this.getX() == o.getX() && this.getZ() == o.getZ() && this.getWorld() == o.getWorld();
	}

	public void clear() {

		setTown(null);
		setResident(null);
		setWorld(null);
	}

	@Override
	public String toString() {

		return getWorld().getName() + " (" + getCoord() + ")";
	}

	public boolean isWarZone() {

		return getWorld().isWarZone(getCoord());
	}

	public boolean isJail() {

		return this.getType() == TownBlockType.JAIL;
	}
	
	public void addMetaData(CustomDataField md) {
		if (getMetadata() == null)
			metadata = new HashSet<>();
		
		getMetadata().add(md);
		save();
	}
	
	public void removeMetaData(CustomDataField md) {
		if (!hasMeta())
			return;
		
		getMetadata().remove(md);

		if (getMetadata().size() == 0)
			this.metadata = null;
		save();
	}

	public HashSet<CustomDataField<?>> getMetadata() {
		return metadata;
	}

	public boolean hasMeta() {
		return getMetadata() != null;
	}

	public void setMetadata(String str) {
		
		if (metadata == null)
			metadata = new HashSet<>();
		
		String[] objects = str.split(";");
		for (String object : objects) {
			metadata.add(CustomDataField.load(object));
		}
	}

	public boolean hasPlotObjectGroup() { return plotGroup != null; }
	
	public PlotGroup getPlotObjectGroup() {
		return plotGroup;
	}
	
	public void removePlotObjectGroup() {
		this.plotGroup = null;
	}

	public void setPlotObjectGroup(PlotGroup group) {
		this.plotGroup = group;

		try {
			group.addTownBlock(this);
		} catch (NullPointerException e) {
			TownyMessaging.sendErrorMsg("Townblock failed to setPlotObjectGroup(group), group is null. " + group);
		}
	}

	@Override
	public File getSaveDirectory() {
		return new File(Towny.getPlugin().getDataFolder() + "/data/townblocks/");
	}

	@Override
	public String getSQLTable() {
		return null;
	}

	public UUID getTownID() {
		return townID;
	}
}