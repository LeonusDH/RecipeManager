package fr.synepixel.recipemanager.ui;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;

public class RecipeUI {

    private Player player;

    public RecipeUI(Player player) {
        this.player = player;
    }

    public void generateButtons() {
        Inventory inventory = player.getInventory();
        inventory.clear();
    }
}
