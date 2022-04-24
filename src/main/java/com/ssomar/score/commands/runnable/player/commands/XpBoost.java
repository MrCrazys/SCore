package com.ssomar.score.commands.runnable.player.commands;

import com.ssomar.score.SCore;
import com.ssomar.score.commands.runnable.ActionInfo;
import com.ssomar.score.commands.runnable.player.PlayerCommand;
import com.ssomar.score.events.StunEvent;
import com.ssomar.score.usedapi.WorldGuardAPI;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/* BURN {timeinsecs} */
public class XpBoost extends PlayerCommand{

	private static XpBoost instance;
	@Getter
	private Map<UUID, Double> activeBoosts;

	public XpBoost() {
		activeBoosts = new HashMap<>();
	}

	@Override
	public void run(Player p, Player receiver, List<String> args, ActionInfo aInfo) {
		try {
			double multiplier = Double.valueOf(args.get(0));
			int time = Integer.valueOf(args.get(1));

			activeBoosts.put(receiver.getUniqueId(), multiplier);

			BukkitRunnable runnable3 = new BukkitRunnable() {
				@Override
				public void run() {
					activeBoosts.remove(receiver.getUniqueId());
				}
			};
			runnable3.runTaskLater(SCore.plugin, time*20);

		}catch(Exception ignored) {}
	}

	@Override
	public String verify(List<String> args) {
		String error = "";

		String xpboost = "XPBOOST {multiplier} {timeinsecs}";
		if(args.size() > 2) error = tooManyArgs+xpboost;
		else if(args.size() < 2) error = notEnoughArgs+xpboost;
		else if(!args.get(0).contains("%")){
			try {
				Double.valueOf(args.get(0));
			} catch (NumberFormatException e) {
				error = invalidTime + args.get(0) + " for command: " + xpboost;
			}
		}
		else if(!args.get(1).contains("%")){
			try {
				Integer.valueOf(args.get(0));
			} catch (NumberFormatException e) {
				error = invalidTime + args.get(0) + " for command: " + xpboost;
			}
		}

		return error;
	}

	@Override
	public List<String> getNames() {
		List<String> names = new ArrayList<>();
		names.add("XPBOOST");
		return names;
	}

	@Override
	public String getTemplate() {
		return "XPBOOST {multiplier} {timeinsecs}";
	}

	@Override
	public ChatColor getColor() {
		return null;
	}

	@Override
	public ChatColor getExtraColor() {
		return null;
	}

	public static XpBoost getInstance() {
		if(instance == null) instance = new XpBoost();
		return instance;
	}
}