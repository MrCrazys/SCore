package com.ssomar.score.commands.runnable.player.commands;

import com.ssomar.score.commands.runnable.CommandSetting;
import com.ssomar.score.commands.runnable.SCommandToExec;
import com.ssomar.score.commands.runnable.player.PlayerCommand;
import com.ssomar.score.utils.strings.StringConverter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SetItemlore extends PlayerCommand {

    public SetItemlore() {
        CommandSetting slot = new CommandSetting("slot", 0, Integer.class, -1);
        slot.setSlot(true);
        CommandSetting line = new CommandSetting("line", 1, Integer.class, 1);
        CommandSetting text = new CommandSetting("text", 2, String.class, "&6New_lore_line");
        text.setAcceptUnderScoreForLongText(true);
        List<CommandSetting> settings = getSettings();
        settings.add(slot);
        settings.add(line);
        settings.add(text);
        setNewSettingsMode(true);
    }

    @Override
    public void run(Player p, Player receiver, SCommandToExec sCommandToExec) {
        ItemStack item;
        ItemMeta itemmeta;
        int line = (int) sCommandToExec.getSettingValue("line");
        String text = (String) sCommandToExec.getSettingValue("text");
        int slot = (int) sCommandToExec.getSettingValue("slot");
        if (slot == -1) item = receiver.getInventory().getItemInMainHand();
        else item = receiver.getInventory().getItem(slot);

        if (item == null || item.getType() == Material.AIR) return;
        if(!item.hasItemMeta()){
            item.setItemMeta(new ItemStack(item.getType()).getItemMeta());
        }

        itemmeta = item.getItemMeta();

        List<String> args = sCommandToExec.getOtherArgs();
        StringBuilder message = new StringBuilder(text);
        message.append(" ");
        for (String s : args) {
            //SsomarDev.testMsg("cmdarg> "+s);
            message.append(s).append(" ");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));

        List<String> list = itemmeta.getLore();

        if (list == null) return;
        if (list.size() < line) return;

        if (line > 0) line += -1;
        list.set(line, StringConverter.coloredString(message.toString()));

        itemmeta.setLore(list);
        item.setItemMeta(itemmeta);
    }


    @Override
    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        names.add("SET_ITEM_LORE");
        names.add("SET_LORE");
        names.add("SETLORE");
        return names;
    }

    @Override
    public String getTemplate() {
        return "SET_ITEM_LORE slot:-1 line:1 text:&6New_lore_line";
    }

    @Override
    public ChatColor getColor() {
        return null;
    }

    @Override
    public ChatColor getExtraColor() {
        return null;
    }
}
