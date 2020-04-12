package fr.synepixel.recipemanager.recipe;

import com.flowpowered.nbt.CompoundMap;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.item.recipe.Recipe;

import java.util.*;

public abstract class CustomRecipeRegistry<T extends Recipe> {

    private static Set<CustomRecipeRegistry<? extends Recipe>> registries = new HashSet<>();

    private List<T> recipes = new ArrayList<>();
    private String key;

    public CustomRecipeRegistry(String key){
        this.key = key;
    }

    public void addRecipe(T recipe){
        recipes.add(recipe);
    }

    public void removeRecipe(T recipe){
        recipes.remove(recipe);
    }

    public abstract void inject(GameRegistry gameRegistry);

    public List<T> getRecipes() {
        return recipes;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof CustomRecipeRegistry)) return false;
        CustomRecipeRegistry<?> that = (CustomRecipeRegistry<?>) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public abstract void save(CompoundMap map);
    public abstract void load(CompoundMap map);

    public static void addRegistry(CustomRecipeRegistry<? extends Recipe> registry){
        registries.add(registry);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe> CustomRecipeRegistry<T> getRegistry(String name){
        for(CustomRecipeRegistry<? extends Recipe> registry : registries){
            if(registry.getKey().equals(name)) return (CustomRecipeRegistry<T>) registry;
        }
        return null;
    }

    public static Set<CustomRecipeRegistry<? extends Recipe>> getRegistries() {
        return registries;
    }
}
