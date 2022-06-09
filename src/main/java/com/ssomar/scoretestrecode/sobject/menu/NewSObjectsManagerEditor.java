package com.ssomar.scoretestrecode.sobject.menu;

import com.ssomar.executableitems.configs.ingame.items.DefaultItemsGUI;
import com.ssomar.score.menu.GUI;
import com.ssomar.scoretestrecode.editor.NewGUIManager;
import com.ssomar.scoretestrecode.editor.NewInteractionClickedGUIManager;
import org.bukkit.entity.Player;

public class NewSObjectsManagerEditor extends NewGUIManager<NewSObjectsEditorAbstract> {

	private static NewSObjectsManagerEditor instance;

	public void startEditing(Player editor, NewSObjectsEditorAbstract gui) {
		cache.put(editor, gui);
		gui.openGUISync(editor);
	}

	@Override
	public boolean allClicked(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> i) {
		if(i.decoloredName.contains("FOLDER: ")){
			i.gui.goToFolder(i.name);
		}
		else if(i.decoloredName.contains("Path")){
			i.gui.goBack();
		}
		else if(i.coloredDeconvertName.contains(NewSObjectsEditorAbstract.NEW)){
			i.gui.sendMessageCreate(i.player);
		}
		else if(i.decoloredName.contains("Default Premium") || i.name.contains(" from Custom packs")){
			new DefaultItemsGUI(i.player).openGUISync(i.player);
		}
		else if(i.coloredDeconvertName.contains(NewSObjectsEditorAbstract.COLOR_OBJECT_ID)){
			i.gui.openEditorSObject(i.decoloredName.split(GUI.OBJECT_ID)[1].trim(), i.player);
		}
		else return false;
		return true;
	}

	@Override
	public boolean noShiftclicked(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> i) {
		return false;
	}

	@Override
	public boolean noShiftLeftclicked(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> i) {
		return false;
	}

	@Override
	public boolean noShiftRightclicked(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> i) {
		return false;
	}

	@Override
	public boolean shiftClicked(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> i) {
		return false;
	}

	@Override
	public boolean shiftLeftClicked(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> i) {
		if(i.decoloredName.contains(GUI.OBJECT_ID)) {
			i.player.closeInventory();
			String id = i.decoloredName.split(GUI.OBJECT_ID)[1];
			i.gui.sendMessageDelete(id, i.player);
			return true;
		}

		return false;
	}

	@Override
	public boolean shiftRightClicked(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> i) {
		if(i.decoloredName.contains(GUI.OBJECT_ID) && !i.decoloredName.contains("ERROR ID")) {
			i.gui.giveSObject(i.decoloredName.split(GUI.OBJECT_ID)[1], i.player);
			return true;
		}
		return false;
	}

	@Override
	public boolean leftClicked(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> i) {
		return false;
	}

	@Override
	public boolean rightClicked(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {
		return false;
	}

	@Override
	public void receiveMessage(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {

	}

	@Override
	public void receiveMessagePreviousPage(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {

	}

	@Override
	public void receiveMessageNextPage(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {

	}

	@Override
	public void receiveMessageFinish(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {

	}

	@Override
	public void receiveMessageValue(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {

	}

	@Override
	public void newObject(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {

	}

	@Override
	public void reset(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {

	}

	@Override
	public void back(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {

	}

	@Override
	public void nextPage(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {
		interact.gui.goNextPage();
	}

	@Override
	public void previousPage(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {
		interact.gui.goPreviousPage();
	}

	@Override
	public void save(NewInteractionClickedGUIManager<NewSObjectsEditorAbstract> interact) {

	}

	public static NewSObjectsManagerEditor getInstance(){
		if(instance == null) instance = new NewSObjectsManagerEditor();
		return instance;
	}
}