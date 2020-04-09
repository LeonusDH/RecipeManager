package fr.synepixel.recipemanager;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;

public class Main {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartingServerEvent event) {

    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        logger.info("Crafting plugin stopped !");
    }
}
