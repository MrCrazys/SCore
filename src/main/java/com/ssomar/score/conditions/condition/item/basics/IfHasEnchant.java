package com.ssomar.score.conditions.condition.item.basics;

import com.iridium.iridiumskyblock.dependencies.ormlite.stmt.query.In;
import com.ssomar.executableitems.items.ExecutableItem;
import com.ssomar.score.conditions.condition.conditiontype.ConditionType;
import com.ssomar.score.conditions.condition.item.ItemCondition;
import com.ssomar.score.utils.SendMessage;
import com.ssomar.score.utils.StringCalculation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class IfHasEnchant extends ItemCondition<Map<Enchantment, Integer>, Map<String, String>> {


    public IfHasEnchant() {
        super(ConditionType.MAP_ENCHANT_AMOUNT, "ifHasEnchant", "If has enchant", new String[]{}, new HashMap<>(), " &cThis item must have the good enchantments to active the activator: &6%activator% &cof this item!");
    }

    @Override
    public boolean verifCondition(ItemStack itemStack, Optional<Player> playerOpt, SendMessage messageSender) {

        if(isDefined()){

            ItemMeta itemMeta = null;
            boolean hasItemMeta = itemStack.hasItemMeta();
            if(hasItemMeta) itemMeta = itemStack.getItemMeta();

            if(!hasItemMeta){
                sendErrorMsg(playerOpt, messageSender);
                return false;
            }
            Map<Enchantment, Integer> enchants = itemMeta.getEnchants();
            Map<Enchantment, Integer> condition = getAllCondition(messageSender.getSp());
            for(Enchantment enchant : condition.keySet()){
                if(!enchants.containsKey(enchant) || !Objects.equals(condition.get(enchant), enchants.get(enchant))){
                    sendErrorMsg(playerOpt, messageSender);
                    return false;
                }
            }
        }

        return true;
    }
}