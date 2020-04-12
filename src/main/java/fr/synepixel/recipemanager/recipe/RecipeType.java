package fr.synepixel.recipemanager.recipe;

import fr.synepixel.recipemanager.Main;
import fr.synepixel.recipemanager.ui.CraftingUI;
import fr.synepixel.recipemanager.ui.RecipeUI;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class RecipeType<T extends Recipe> {

    private static Map<String, RecipeType<?>> map = new HashMap<>();

    private BiFunction<Main, Player, RecipeUI<T>> uiFunction;

    public RecipeType(BiFunction<Main, Player, RecipeUI<T>> uiFunction){
        this.uiFunction = uiFunction;
    }

    public RecipeUI<T> createUI(Main main, Player player){
        return uiFunction.apply(main, player);
    }

    public static Map<String, RecipeType<?>> getMap(){
        return map;
    }

    private static <T extends Recipe> RecipeType<T> register(String name, RecipeType<T> recipeType){
        map.put(name, recipeType);
        return recipeType;
    }

    public static final RecipeType<CraftingRecipe> CRAFTING = register("crafting", new RecipeType<>(CraftingUI::new));
}
