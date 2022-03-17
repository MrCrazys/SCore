package com.ssomar.score.menu.particles;

import com.ssomar.score.menu.GUI;
import com.ssomar.score.menu.GUIAbstract;
import com.ssomar.score.sparticles.SParticle;
import com.ssomar.score.sparticles.SParticles;
import com.ssomar.score.splugin.SPlugin;
import com.ssomar.score.utils.StringConverter;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SParticleGUI extends GUIAbstract {

    private boolean newRequiredEI = false;
    public static final String ID = "ID:";
    public static final String TYPE = "Particle Type";
    public static final String AMOUNT = "Particle Amount";
    public static final String OFFSET = "Particle OffSet";
    public static final String SPEED = "Particle Speed";
    public static final String DELAY = "Particle Delay";
    @Getter
    private SParticles sParticles;
    @Getter
    private GUI guiFrom;

    public SParticleGUI(SPlugin sPlugin, SParticles sParticles, GUI guiFrom) {
        super("&8&l" + sPlugin.getShortName() + " Editor - Particle", 4 * 9, sPlugin, null, null);
        this.sParticles = sParticles;
        this.guiFrom = guiFrom;
        newRequiredEI = true;

        int id = 1;
        List<SParticle> particles = sParticles.getParticles();
        for (int i = 0; i < particles.size(); i++) {
            for (SParticle sParticle : particles) {
                if (sParticle.getId().equals(id+"")) {
                    id++;
                }
            }
        }
        String idStr = id + "";
        SParticle sParticle = new SParticle(idStr);
        this.fillTheGUI(sParticle);
    }

    public SParticleGUI(SPlugin sPlugin, SParticles sParticles, SParticle sParticle, GUI guiFrom) {
        super("&8&l" + sPlugin.getShortName() + " Editor - Particle", 4 * 9, sPlugin, null, null);
        this.sParticles = sParticles;
        this.guiFrom = guiFrom;
        this.fillTheGUI(sParticle);
    }

    public void fillTheGUI(SParticle sParticle) {
        //Main Options
        createItem(Material.NETHER_STAR, 1, 0, TITLE_COLOR + TYPE, false, false, "", "&a✎ Click here to change", "&7actually:");
        this.updateType(sParticle.getParticlesType());

        createItem(Material.HOPPER, 1, 1, TITLE_COLOR + AMOUNT, false, false, "", "&a✎ Click here to change", "&7actually:");
        this.updateInt(AMOUNT, sParticle.getParticlesAmount());

        createItem(Material.LEVER, 1, 2, TITLE_COLOR + OFFSET, false, false, "", "&a✎ Click here to change", "&7actually:");
        this.updateDouble(OFFSET, sParticle.getParticlesOffSet());

        createItem(Material.LEVER, 1, 3, TITLE_COLOR + SPEED, false, false, "", "&a✎ Click here to change", "&7actually:");
        this.updateDouble(SPEED, sParticle.getParticlesSpeed());

        createItem(Material.LEVER, 1, 4, TITLE_COLOR + DELAY, false, false, "", "&a✎ Click here to change", "&7actually:");
        this.updateInt(DELAY, sParticle.getParticlesDelay());


        createItem(Material.BOOK, 1, 8, "&a&l" + ID, false, false, "", "&7actually: &e" + sParticle.getId());

        //Reset menu
        createItem(ORANGE, 1, 28, "&4&l✘ &cReset", false, false, "", "&c&oClick here to reset", "&c&oall options of this particle");
        // exit
        createItem(RED, 1, 27, "&4&l▶&c Back to the list of particle", false, false);
        //Save menu
        createItem(GREEN, 1, 35, "&2&l✔ &aSave this particle", false, false, "", "&a&oClick here to save this", "&a&oparticle");
    }


    public Particle nextType(Particle particle) {
        boolean next = false;
        for (Particle check : Particle.values()) {
            if (check.equals(particle)) {
                next = true;
                continue;
            }
            if (next) return check;
        }
        return Particle.values()[0];
    }

    public Particle prevType(Particle particle) {
        int i = -1;
        int cpt = 0;
        for (Particle check : Particle.values()) {
            if (check.equals(particle)) {
                i = cpt;
                break;
            }
            cpt++;
        }
        if (i == 0) return Particle.values()[Particle.values().length - 1];
        else return Particle.values()[cpt - 1];
    }

    public void updateType(Particle particle) {
        ItemStack item = this.getByName(TYPE);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore().subList(0, 2);
        boolean find = false;
        for (Particle check : Particle.values()) {
            if (particle.equals(check)) {
                lore.add(StringConverter.coloredString("&2➤ &a" + particle));
                find = true;
            } else if (find) {
                if (lore.size() == 17) break;
                lore.add(StringConverter.coloredString("&6✦ &e" + check));
            }
        }
        for (Particle check : Particle.values()) {
            if (lore.size() == 17) break;
            else {
                lore.add(StringConverter.coloredString("&6✦ &e" + check));
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        /* Update the gui only for the right click , for the left it updated automaticaly idk why */
        for (HumanEntity e : this.getInv().getViewers()) {
            if (e instanceof Player) {
                Player p = (Player) e;
                p.updateInventory();
            }
        }
    }

    public Particle getType() {
        ItemStack item = this.getByName(TYPE);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        for (String str : lore) {
            if (str.contains("➤ ")) {
                str = StringConverter.decoloredString(str).replaceAll(" Premium", "");
                return Particle.valueOf(str.split("➤ ")[1]);
            }
        }
        return null;
    }


    public boolean isNewRequiredEI() {
        return newRequiredEI;
    }

    public void setNewRequiredEI(boolean newRequiredEI) {
        this.newRequiredEI = newRequiredEI;
    }

    @Override
    public void reloadGUI() {

    }
}