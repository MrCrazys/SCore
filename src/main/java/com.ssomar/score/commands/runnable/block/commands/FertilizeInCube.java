package com.ssomar.score.commands.runnable.block.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import com.ssomar.score.commands.runnable.ActionInfo;
import com.ssomar.score.commands.runnable.block.BlockCommand;
import com.ssomar.score.utils.ToolsListMaterial;
import org.jetbrains.annotations.NotNull;

/* FARMINCUBE {radius} {ActiveDrop true or false} */
public class FertilizeInCube extends BlockCommand {

    @Override
    public void run(Player p, @NotNull Block block, Material oldMaterial, List<String> args, ActionInfo aInfo) {
        List<Material> validMaterial = ToolsListMaterial.getInstance().getPlantWithGrowth();

        try {
            int radius = Integer.parseInt(args.get(0));
            /* Fertilize only the block selected */
            if (radius == 0) {
                Block toDestroy = block;
                if (!validMaterial.contains(toDestroy.getType())) return;
                this.grownUpTheBlock(toDestroy, p);
            } else {

                for (int y = -radius; y < radius + 1; y++) {
                    for (int x = -radius; x < radius + 1; x++) {
                        for (int z = -radius; z < radius + 1; z++) {

                            Block toDestroy = block.getWorld().getBlockAt(block.getX() + x, block.getY() + y, block.getZ() + z);

                            if (!validMaterial.contains(toDestroy.getType())) continue;


                            this.grownUpTheBlock(toDestroy, p);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void grownUpTheBlock(Block toGrownUp, Player p) {

        BlockData data = toGrownUp.getState().getBlockData();

        if (data instanceof Ageable) {
            Ageable ageable = (Ageable) data;
            if (ageable.getAge() != ageable.getMaximumAge()) {
                ageable.setAge(ageable.getAge() + 1);
            }
        }

        toGrownUp.setBlockData(data);
    }


    @Override
    public Optional<String> verify(List<String> args, boolean isFinalVerification) {
        String error = "";

        String fertilize = "FERTILIZEINCUBE {range}";
        if (args.size() > 1) error = tooManyArgs + fertilize;
        else if (args.size() < 1) error = notEnoughArgs + fertilize;
        else if (args.size() == 1) {
            try {
                Integer.valueOf(args.get(0));
            } catch (NumberFormatException e) {
                error = invalidRange + args.get(0) + " for command: " + fertilize;
            }
        }

        return  error.isEmpty() ? Optional.empty() : Optional.of(error);
    }

    @Override
    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        names.add("FERTILIZEINCUBE");
        return names;
    }

    @Override
    public String getTemplate() {
        return "FERTILIZEINCUBE {radius}";
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