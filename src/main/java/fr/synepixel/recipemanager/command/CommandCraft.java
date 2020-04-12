package fr.synepixel.recipemanager.command;

import fr.synepixel.recipemanager.Main;
import fr.synepixel.recipemanager.recipe.RecipeType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandCraft implements CommandExecutor {

    private Main plugin;

    public CommandCraft(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            src.sendMessage(Text.builder("Only players can use this").color(TextColors.RED).build());
            return CommandResult.empty();
        }
        Player player = (Player) src;
        args.<RecipeType<?>>getOne("type").ifPresent(type -> type.createUI(plugin, player).generateButtons().open());
        return CommandResult.success();
    }
}
