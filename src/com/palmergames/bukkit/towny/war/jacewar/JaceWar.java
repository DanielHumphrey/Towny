package com.palmergames.bukkit.towny.war.flagwar;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.Translation;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.utils.AreaSelectionUtil;
import com.palmergames.bukkit.towny.war.flagwar.events.CellAttackCanceledEvent;
import com.palmergames.bukkit.towny.war.flagwar.events.CellAttackEvent;
import com.palmergames.bukkit.towny.war.flagwar.events.CellDefendedEvent;
import com.palmergames.bukkit.towny.war.flagwar.events.CellWonEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JaceWar {
	
	public List<Town> attackers;
	public TownUnderAttack defending;
	public static List<TownUnderAttack> allUnderAttack;
	
	
	public JaceWar(Town attacker, Town defender) {
		attackers.add(attacker);
		defending = (TownUnderAttack)defender;
		
		// populate attacker's ally list (so its not just one nation attacking)
		for (int i = 0; i < attacker.nation.allies.length; i ++) {
			
			if(VerifyNoDoubleAllies(attacker.nation.allies[i], defender)) {
				attackers.add(attacker.nation.allies[i]);
			}
		}
	}
	
	
	void BeginAttack(Town attacker, TownUnderAttack defender) {
		
		
		
	}
	
	
	
	boolean VerifyNoDoubleAllies(Nation one, Nation two) {
		
		for(int i = 0; i < one.allies.length; i++) {
			
			if(one.allies[i].uuid == two.uuid) {
				return false;
			}
			else {
				continue;
			}
			
			return true;
			
		}
		
	}
	

}
