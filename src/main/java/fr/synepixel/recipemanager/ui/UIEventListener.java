package fr.synepixel.recipemanager.ui;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;

public class UIEventListener {

    @Listener
    public void onClick(ClickInventoryEvent event){
        RecipeUI<?> ui = RecipeUI.getInterface(event.getCause().first(Player.class).get().getUniqueId());
        if(ui != null) event.setCancelled(ui.onClick(event));
    }
}
