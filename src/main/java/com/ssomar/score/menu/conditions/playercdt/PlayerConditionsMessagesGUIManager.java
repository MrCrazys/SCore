package com.ssomar.score.menu.conditions.playercdt;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ssomar.score.linkedplugins.LinkedPlugins;
import com.ssomar.score.menu.conditions.ConditionsGUIManager;
import com.ssomar.score.menu.conditions.RequestMessage;
import com.ssomar.score.menu.conditions.playercdt.PlayerConditionsMessagesGUI.PlayerConditionsMessages;
import com.ssomar.score.menu.score.GUIManagerSCore;
import com.ssomar.score.menu.score.InteractionClickedGUIManager;
import com.ssomar.score.sobject.SObject;
import com.ssomar.score.sobject.sactivator.SActivator;
import com.ssomar.score.sobject.sactivator.conditions.PlayerConditions;
import com.ssomar.score.splugin.SPlugin;
import com.ssomar.score.utils.StringConverter;


public class PlayerConditionsMessagesGUIManager extends GUIManagerSCore<PlayerConditionsMessagesGUI>{

	private static PlayerConditionsMessagesGUIManager instance;	

	public void startEditing(Player p, SPlugin sPlugin, SObject sObject, SActivator sActivator, PlayerConditions pC, String detail) {
		cache.put(p, new PlayerConditionsMessagesGUI(sPlugin, sObject, sActivator, pC, detail));
		cache.get(p).openGUISync(p);
	}
	
	@Override
	public void clicked(InteractionClickedGUIManager<PlayerConditionsMessagesGUI> i) {
		if(i.name.contains("Save")) {
			savePlayerConditionsEI(i.player);
			i.sObject = LinkedPlugins.getSObject(i.sPlugin, i.sObject.getID());
			ConditionsGUIManager.getInstance().startEditing(i.player, i.sPlugin, i.sObject, i.sObject.getActivator(i.sActivator.getID()));
		}

		else if(i.name.contains("Back")) {
			ConditionsGUIManager.getInstance().startEditing(i.player, i.sPlugin, i.sObject, i.sActivator);
		}
		
		else if(!i.name.isEmpty()) {
			for(PlayerConditionsMessages pcMsg : PlayerConditionsMessages.values()) {
				if(i.name.contains(pcMsg.name)) {
					requestWriting.put(i.player, pcMsg.name);
					i.msgInfos.actualMsg = cache.get(i.player).getActuallyWithColor(pcMsg.name);
					RequestMessage.sendRequestMessage(i.msgInfos);
				}
			}
		}
	}
	
	public void shiftClicked(Player p, ItemStack item) {
		if(item != null && item.hasItemMeta()) {
				SPlugin sPlugin = cache.get(p).getsPlugin();
				SObject sObject = cache.get(p).getSObject();
				SActivator sAct = cache.get(p).getSAct();
				String name = StringConverter.decoloredString(item.getItemMeta().getDisplayName());
				//String plName = sPlugin.getNameDesign();

				if(name.contains("Reset")) {
					cache.replace(p, new PlayerConditionsMessagesGUI(sPlugin, sObject, sAct, new PlayerConditions(), cache.get(p).getDetail()));
					cache.get(p).openGUISync(p);
				}

				else if(name.contains("Save")) {
					savePlayerConditionsEI(p);
					sObject = LinkedPlugins.getSObject(sPlugin, sObject.getID());
					ConditionsGUIManager.getInstance().startEditing(p, sPlugin, sObject, sObject.getActivator(sAct.getID()));
				}

				else if(name.contains("Exit")) {
					p.closeInventory();
				}

				else if(name.contains("Back")) {
					ConditionsGUIManager.getInstance().startEditing(p, sPlugin, sObject, sAct);
				}
				else {
					String detail = cache.get(p).getDetail();
					savePlayerConditionsEI(p);
					sObject = LinkedPlugins.getSObject(sPlugin, sObject.getID());
					if(detail.contains("owner"))PlayerConditionsGUIManager.getInstance().startEditing(p, sPlugin, sObject, sAct, sObject.getActivator(sAct.getID()).getOwnerConditions(), detail);
					else if(detail.contains("target")) PlayerConditionsGUIManager.getInstance().startEditing(p, sPlugin, sObject, sAct, sObject.getActivator(sAct.getID()).getTargetPlayerConditions(), detail);
					else if(detail.contains("player")) PlayerConditionsGUIManager.getInstance().startEditing(p, sPlugin, sObject, sAct, sObject.getActivator(sAct.getID()).getPlayerConditions(), detail);
				}
		}
	}

	public void receivedMessage(Player p, String message) {
		//SPlugin sPlugin = cache.get(p).getsPlugin();
		//SObject sObject = cache.get(p).getSObject();
		//SActivator sAct = cache.get(p).getSAct();
		//String plName = sPlugin.getNameDesign();

		String request = requestWriting.get(p);

		if(message .contains("NO MESSAGE")) cache.get(p).updateMessage(request, "");
		else cache.get(p).updateMessage(request, message);
		requestWriting.remove(p);
		cache.get(p).openGUISync(p);
	}

	public void savePlayerConditionsEI(Player p) {
		SPlugin sPlugin = cache.get(p).getsPlugin();
		SObject sObject = cache.get(p).getSObject();
		SActivator sActivator = cache.get(p).getSAct();
		PlayerConditions pC = (PlayerConditions)cache.get(p).getConditions();

		pC.setIfBlockingMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_BLOCKING_MSG.name));
		pC.setIfNotBlockingMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_NOT_BLOCKING_MSG.name));
		pC.setIfFlyingMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_FLYING_MSG.name));
		pC.setIfIsInTheAirMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_IS_IN_THE_AIR_MSG.name));
		pC.setIfIsOnTheBlockMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_IS_ON_THE_BLOCK_MSG.name));
		pC.setIfGlidingMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_GLIDING_MSG.name));
		pC.setIfHasPermissionMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_HAS_PERMISSION_MSG.name));
		pC.setIfInBiomeMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_IN_BIOME_MSG.name));
		pC.setIfInRegionMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_IN_REGION_MSG.name));
		pC.setIfInWorldMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_IN_WORLD_MSG.name));
		pC.setIfLightLevelMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_LIGHT_LEVEL_MSG.name));
		pC.setIfNotHasPermissionMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_NOT_HAS_PERMISSION_MSG.name));
		pC.setIfNotInBiomeMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_NOT_IN_BIOME_MSG.name));
		pC.setIfNotInRegionMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_NOT_IN_REGION_MSG.name));
		pC.setIfNotInWorldMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_NOT_IN_WORLD_MSG.name));
		pC.setIfNotSneakingMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_NOT_SNEAKING_MSG.name));
		pC.setIfNotTargetBlockMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_NOT_TARGET_BLOCK_MSG.name));
		pC.setIfPlayerEXPMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_PLAYER_EXP_MSG.name));
		pC.setIfPlayerFoodLevelMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_PLAYER_FOOD_LEVEL_MSG.name));
		pC.setIfPlayerHealthMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_PLAYER_HEALTH_MSG.name));
		pC.setIfPlayerLevelMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_PLAYER_LEVEL_MSG.name));
		pC.setIfSneakingMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_SNEAKING_MSG.name));
		pC.setIfSwimmingMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_SWIMMING_MSG.name));
		pC.setIfTargetBlockMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_TARGET_BLOCK_MSG.name));
		pC.setIfPosXMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_POS_X_MSG.name));
		pC.setIfPosYMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_POS_Y_MSG.name));
		pC.setIfPosZMsg(cache.get(p).getMessage(PlayerConditionsMessages.IF_POS_Z_MSG.name));

		PlayerConditions.savePlayerConditions(sPlugin, sObject, sActivator, pC, cache.get(p).getDetail());
		cache.remove(p);
		requestWriting.remove(p);
		LinkedPlugins.reloadSObject(sPlugin, sObject.getID());
	}


	public static PlayerConditionsMessagesGUIManager getInstance() {
		if(instance == null) instance = new PlayerConditionsMessagesGUIManager();
		return instance;
	}
}