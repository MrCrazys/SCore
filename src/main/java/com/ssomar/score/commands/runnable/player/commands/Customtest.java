package com.ssomar.score.commands.runnable.player.commands;

import com.ssomar.score.commands.runnable.ActionInfo;
import com.ssomar.score.commands.runnable.player.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* TOTEM_ANIMATION */
public class Customtest extends PlayerCommand {

    @Override
    public void run(Player p, Player receiver, List<String> args, ActionInfo aInfo) {

    }

    @Override
    public Optional<String> verify(List<String> args, boolean isFinalVerification) {
        return Optional.empty();
    }

    @Override
    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        names.add("CUSTOMTEST");
        return names;
    }

    @Override
    public String getTemplate() {
        return "CUSTOMTEST";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public ChatColor getExtraColor() {
        return ChatColor.GOLD;
    }
}