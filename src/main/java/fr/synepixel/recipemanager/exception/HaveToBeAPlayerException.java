package fr.synepixel.recipemanager.exception;

import org.spongepowered.api.command.CommandSource;

public class HaveToBeAPlayerException extends PluginException {
    public HaveToBeAPlayerException(CommandSource src) {
        super(src, "Sorry, but you have to be a player to execute this command !");
    }
}
