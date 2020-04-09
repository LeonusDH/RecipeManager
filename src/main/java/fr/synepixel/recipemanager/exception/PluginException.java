package fr.synepixel.recipemanager.exception;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PluginException extends CommandException {

    public PluginException(CommandSource src, String text) {
        super(Text.of());
        src.sendMessage(Text.builder(text).color(TextColors.RED).build());
    }

}
