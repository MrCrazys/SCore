package com.ssomar.score.commands.runnable.block.commands;

import com.ssomar.score.commands.runnable.SCommandToExec;
import com.ssomar.score.commands.runnable.block.BlockCommand;
import com.ssomar.score.utils.logging.Utils;
import com.ssomar.score.utils.strings.StringConverter;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsoleMessageBlock extends BlockCommand {

    @Override
    public void run(@Nullable Player p, @NotNull Block block, SCommandToExec sCommandToExec) {
        List<String> args = sCommandToExec.getOtherArgs();
        StringBuilder build = new StringBuilder();

        for (String arg : args) {
            build.append(StringConverter.coloredString(arg)).append(" ");
        }

        Utils.sendConsoleMsg(build.toString());
    }

    @Override
    public Optional<String> verify(List<String> args, boolean isFinalVerification){
        if (args.size() < 1) return Optional.of(notEnoughArgs + getTemplate());
        return Optional.empty();
    }

    @Override
    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        names.add("CONSOLEMESSAGE");
        return names;
    }

    @Override
    public String getTemplate() {
        return "CONSOLEMESSAGE {text}";
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
