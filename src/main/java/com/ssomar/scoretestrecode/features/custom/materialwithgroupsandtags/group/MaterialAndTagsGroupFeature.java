package com.ssomar.scoretestrecode.features.custom.materialwithgroupsandtags.group;

import com.ssomar.score.menu.GUI;
import com.ssomar.score.splugin.SPlugin;
import com.ssomar.scoretestrecode.editor.NewGUIManager;
import com.ssomar.scoretestrecode.features.FeatureInterface;
import com.ssomar.scoretestrecode.features.FeatureParentInterface;
import com.ssomar.scoretestrecode.features.FeatureWithHisOwnEditor;
import com.ssomar.scoretestrecode.features.FeaturesGroup;
import com.ssomar.scoretestrecode.features.custom.materialwithgroupsandtags.materialandtags.MaterialAndTagsFeature;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class MaterialAndTagsGroupFeature extends FeatureWithHisOwnEditor<MaterialAndTagsGroupFeature, MaterialAndTagsGroupFeature, MaterialAndTagsGroupFeatureEditor, MaterialAndTagsGroupFeatureEditorManager> implements FeaturesGroup<MaterialAndTagsFeature> {

    private Map<String, MaterialAndTagsFeature> materialAndTags;

    public MaterialAndTagsGroupFeature(FeatureParentInterface parent, String name, String editorName, String[] editorDescription) {
        super(parent, name, editorName, editorDescription, Material.OBSIDIAN, false);
        reset();
    }

    @Override
    public void reset() {
        this.materialAndTags = new HashMap<>();
    }

    @Override
    public List<String> load(SPlugin plugin, ConfigurationSection config, boolean isPremiumLoading) {
        List<String> error = new ArrayList<>();
        if(config.isConfigurationSection(this.getName())) {
            ConfigurationSection enchantmentsSection = config.getConfigurationSection(this.getName());
            for(String attributeID : enchantmentsSection.getKeys(false)) {
                MaterialAndTagsFeature attribute = new MaterialAndTagsFeature(this, attributeID);
                List<String> subErrors = attribute.load(plugin, enchantmentsSection, isPremiumLoading);
                if (subErrors.size() > 0) {
                    error.addAll(subErrors);
                    continue;
                }
                materialAndTags.put(attributeID, attribute);
            }
        }
        return error;
    }

    @Override
    public void save(ConfigurationSection config) {
        config.set(this.getName(), null);
        ConfigurationSection attributesSection = config.createSection(this.getName());
        for(String enchantmentID : materialAndTags.keySet()) {
            materialAndTags.get(enchantmentID).save(attributesSection);
        }
    }

    @Override
    public MaterialAndTagsGroupFeature getValue() {
        return this;
    }

    @Override
    public MaterialAndTagsGroupFeature initItemParentEditor(GUI gui, int slot) {
        String[] finalDescription = new String[getEditorDescription().length + 2];
        System.arraycopy(getEditorDescription(), 0, finalDescription, 0, getEditorDescription().length);
        finalDescription[finalDescription.length -2] = gui.CLICK_HERE_TO_CHANGE;
        finalDescription[finalDescription.length -1] = "&7&oMaterial(s) added: &e"+ materialAndTags.size();

        gui.createItem(getEditorMaterial(), 1, slot, gui.TITLE_COLOR + getEditorName(), false, false, finalDescription);
        return this;
    }

    @Override
    public void updateItemParentEditor(GUI gui) {

    }

    @Override
    public void extractInfoFromParentEditor(NewGUIManager manager, Player player) {

    }

    @Override
    public MaterialAndTagsGroupFeature clone() {
        MaterialAndTagsGroupFeature eF = new MaterialAndTagsGroupFeature(getParent(), getName(), getEditorName(), getEditorDescription());
        eF.setMaterialAndTags(new HashMap<>(this.getMaterialAndTags()));
        return eF;
    }

    @Override
    public List<FeatureInterface> getFeatures() {
        return new ArrayList<>(materialAndTags.values());
    }

    @Override
    public String getParentInfo() {
        return getParent().getParentInfo();
    }

    @Override
    public ConfigurationSection getConfigurationSection() {
        ConfigurationSection section = getParent().getConfigurationSection();
        if(section.isConfigurationSection(this.getName())) {
            return section.getConfigurationSection(this.getName());
        }
        else return section.createSection(this.getName());
    }

    @Override
    public File getFile() {
        return getParent().getFile();
    }

    @Override
    public void reload() {
        for(FeatureInterface feature : getParent().getFeatures()) {
            if(feature instanceof MaterialAndTagsGroupFeature) {
                MaterialAndTagsGroupFeature eF = (MaterialAndTagsGroupFeature) feature;
                eF.setMaterialAndTags(this.getMaterialAndTags());
                break;
            }
        }
    }

    @Override
    public void openBackEditor(@NotNull Player player) {
        getParent().openEditor(player);
    }

    @Override
    public void openEditor(@NotNull Player player) {
        MaterialAndTagsGroupFeatureEditorManager.getInstance().startEditing(player, this);
    }

    @Override
    public void createNewFeature(@NotNull Player editor) {
        String baseId = "material";
        for(int i = 0; i < 1000; i++) {
            String id = baseId + i;
            if(!materialAndTags.containsKey(id)) {
                MaterialAndTagsFeature eF = new MaterialAndTagsFeature(this, id);
                materialAndTags.put(id, eF);
                eF.openEditor(editor);
                break;
            }
        }
    }

    @Override
    public void deleteFeature(@NotNull Player editor, MaterialAndTagsFeature feature) {
        materialAndTags.remove(feature.getId());
    }

    public boolean isValid(Block block) {
        // #TODO check if block is valid
        return false;
    }

    public boolean isValid(Material type, BlockData blockData) {
        // #TODO check if block is valid
        return false;
    }
}