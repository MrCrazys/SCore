package com.ssomar.score.features.custom.conditions.customei.condition;

import com.ssomar.executableitems.ExecutableItems;
import com.ssomar.score.features.FeatureParentInterface;
import com.ssomar.score.features.custom.conditions.customei.CustomEIConditionFeature;
import com.ssomar.score.features.types.BooleanFeature;
import com.ssomar.score.utils.SendMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.UUID;

public class IfOwnerOfTheEI extends CustomEIConditionFeature<BooleanFeature, IfOwnerOfTheEI> {

    public IfOwnerOfTheEI(FeatureParentInterface parent) {
        super(parent, "ifOwnerOfTheEI", "If owner of the EI", new String[]{}, Material.ANVIL, false);
    }

    @Override
    public boolean verifCondition(Player player, ItemStack itemStack, Optional<Player> playerOpt, SendMessage messageSender, Event event) {
        if (hasCondition()) {
            if (itemStack.hasItemMeta()) {
                ItemMeta iM = itemStack.getItemMeta();

                NamespacedKey key = new NamespacedKey(ExecutableItems.getPluginSt(), "EI-OWNER");
                String uuidStr = iM.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                boolean invalid = false;
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(uuidStr);
                } catch (Exception e) {
                    invalid = true;
                }
                if (invalid || !uuid.equals(player.getUniqueId())) {
                    sendErrorMsg(playerOpt, messageSender);
                    cancelEvent(event);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public IfOwnerOfTheEI getValue() {
        return this;
    }

    @Override
    public void subReset() {
        setCondition(new BooleanFeature(this, "ifOwnerOfTheEI", false, "If owner of the EI", new String[]{}, Material.LEVER, false, true));
    }

    @Override
    public boolean hasCondition() {
        return getCondition().getValue();
    }

    @Override
    public IfOwnerOfTheEI getNewInstance(FeatureParentInterface parent) {
        return new IfOwnerOfTheEI(parent);
    }
}