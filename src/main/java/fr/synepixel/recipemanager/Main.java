package fr.synepixel.recipemanager;

import com.google.inject.Inject;
import fr.synepixel.recipemanager.commands.CommandAddCraft;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(
        name = "RecipesManager",
        id = "recipes_manager",
        version = "1.0",
        description = "A plugin to create recipes",
        authors = "RedsTom, Il_totore",
        dependencies = {},
        url = ""
)
public class Main {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartingServerEvent event) {

        CommandSpec cmd = CommandSpec.builder().executor(new CommandAddCraft()).permission("craft.add").description(Text.of("Add a recipe")).build();
        Sponge.getCommandManager().register(this, cmd, "addcraft", "addrecipe", "add-craft", "add-recipe");

    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        logger.info("Crafting plugin stopped !");
    }
}
