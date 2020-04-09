package fr.synepixel.recipemanager;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

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

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path privateConfigDir;

    @Listener
    public void onServerStart(GameStartingServerEvent event) {

    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        logger.info("Crafting plugin stopped !");
    }
}
