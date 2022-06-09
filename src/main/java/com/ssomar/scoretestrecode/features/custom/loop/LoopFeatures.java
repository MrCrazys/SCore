package com.ssomar.scoretestrecode.features.custom.loop;

import com.ssomar.score.menu.GUI;
import com.ssomar.score.splugin.SPlugin;
import com.ssomar.scoretestrecode.editor.NewGUIManager;
import com.ssomar.scoretestrecode.features.FeatureInterface;
import com.ssomar.scoretestrecode.features.FeatureParentInterface;
import com.ssomar.scoretestrecode.features.FeatureWithHisOwnEditor;
import com.ssomar.scoretestrecode.features.types.BooleanFeature;
import com.ssomar.scoretestrecode.features.types.IntegerFeature;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter @Setter
public class LoopFeatures extends FeatureWithHisOwnEditor<LoopFeatures, LoopFeatures, LoopFeaturesEditor, LoopFeaturesEditorManager> {

    private IntegerFeature delay;
    private BooleanFeature delayInTick;

    public LoopFeatures(FeatureParentInterface parent) {
        super(parent, "loop", "Loop features", new String[]{"&7&oSpecific settings", "&7&ofor the loop activator"}, Material.ANVIL, false);
        reset();
    }

    @Override
    public void reset() {
       this.delay = new IntegerFeature(this, "delay", Optional.of(1), "Delay", new String[]{"&7&oDelay between each activation"}, GUI.CLOCK, false);
       this.delayInTick = new BooleanFeature(this, "delayInTick", false, "Delay in tick", new String[]{"&7&oIs the delay in ticks ?"}, Material.LEVER, false);
    }

    @Override
    public List<String> load(SPlugin plugin, ConfigurationSection config, boolean isPremiumLoading) {
        List<String> errors = new ArrayList<>();
        errors.addAll(this.delay.load(plugin, config, isPremiumLoading));
        errors.addAll(this.delayInTick.load(plugin, config, isPremiumLoading));

        return errors;
    }

    @Override
    public void save(ConfigurationSection config) {
        delay.save(config);
        delayInTick.save(config);
    }

    @Override
    public LoopFeatures getValue() {
        return this;
    }

    @Override
    public LoopFeatures initItemParentEditor(GUI gui, int slot) {
        String[] finalDescription = new String[getEditorDescription().length + 3];
        System.arraycopy(getEditorDescription(), 0, finalDescription, 0, getEditorDescription().length);
        finalDescription[finalDescription.length - 3] = gui.CLICK_HERE_TO_CHANGE;
        finalDescription[finalDescription.length - 2] = "&7Delay: &e"+delay.getValue().get();
        if(delayInTick.getValue()) {
            finalDescription[finalDescription.length - 1] = "&7In ticks: &a&l✔";
        } else {
            finalDescription[finalDescription.length - 1] = "&7In ticks: &c&l✘";
        }

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
    public LoopFeatures clone() {
        LoopFeatures loopFeatures = new LoopFeatures(getParent());
        loopFeatures.setDelay(this.delay.clone());
        loopFeatures.setDelayInTick(this.delayInTick.clone());
        return loopFeatures;
    }

    @Override
    public List<FeatureInterface> getFeatures() {
        return new ArrayList<>(Arrays.asList(delay, delayInTick));
    }

    @Override
    public String getParentInfo() {
        return getParent().getParentInfo();
    }

    @Override
    public ConfigurationSection getConfigurationSection() {
        return getParent().getConfigurationSection();
    }

    @Override
    public File getFile() {
        return getParent().getFile();
    }

    @Override
    public void reload() {
        for(FeatureInterface feature : getParent().getFeatures()) {
            if(feature instanceof LoopFeatures) {
                LoopFeatures loopFeatures = (LoopFeatures) feature;
                loopFeatures.setDelay(this.delay);
                loopFeatures.setDelayInTick(this.delayInTick);
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
        LoopFeaturesEditorManager.getInstance().startEditing(player, this);
    }

}