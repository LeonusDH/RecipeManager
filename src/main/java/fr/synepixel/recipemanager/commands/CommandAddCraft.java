package fr.synepixel.recipemanager.commands;

import fr.synepixel.recipemanager.exceptions.HaveToBeAPlayerException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class CommandAddCraft implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) throw new HaveToBeAPlayerException(src);

        return null;
    }

}
