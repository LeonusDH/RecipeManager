package fr.synepixel.recipemanager.recipe;

import com.flowpowered.nbt.*;
import fr.synepixel.recipemanager.ui.SerializationUtil;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CraftingRecipeRegistry extends CustomRecipeRegistry<CraftingRecipe> {

    public CraftingRecipeRegistry() {
        super("crafting");
    }

    @Override
    public void save(CompoundMap map) {
        List<CompoundTag> recipes = new ArrayList<>();
        for(CraftingRecipe recipe : getRecipes()) {
            CompoundMap recipeMap = new CompoundMap();
            if(recipe instanceof ShapelessCraftingRecipe) {
                saveShapeless((ShapelessCraftingRecipe) recipe, recipeMap);
            } else if(recipe instanceof ShapedCraftingRecipe){
                saveShaped((ShapedCraftingRecipe)recipe, recipeMap);
            }
            recipeMap.put("id", new StringTag("id", recipe.getId()));
            recipes.add(new CompoundTag(recipe.getId(), recipeMap));
            recipeMap.put("result", new StringTag("result", SerializationUtil.serialize(recipe.getExemplaryResult().createStack())));
        }
        map.put(new ListTag<>("recipes", CompoundTag.class, recipes));
    }

    @Override
    public void load(CompoundMap map) {
        @SuppressWarnings("unchecked")
        List<CompoundTag> recipes = (List<CompoundTag>) map.get("recipes").getValue();
        for(CompoundTag recipeTag : recipes) {
            int shape = ((Byte) recipeTag.getValue().get("shape").getValue()).intValue();
            if(shape == 0) loadShapeless(recipeTag.getValue());
            if(shape == 1) loadShaped(recipeTag.getValue());
        }
    }

    private void saveShapeless(ShapelessCraftingRecipe recipe, CompoundMap map) {
        map.put(new ByteTag("shape", (byte) 0));
        List<StringTag> itemsTags = new ArrayList<>();
        for(Ingredient ingredient : recipe.getIngredientPredicates()) {
            ItemStackSnapshot item = ingredient.displayedItems().isEmpty() ? ItemStack.empty().createSnapshot() : ingredient.displayedItems().get(0);
            itemsTags.add(new StringTag("ingredient", SerializationUtil.serialize(item.createStack())));
        }
        map.put(new ListTag<>("ingredients", StringTag.class, itemsTags));
    }

    private void saveShaped(ShapedCraftingRecipe recipe, CompoundMap map) {
        map.put(new ByteTag("shape", (byte) 1));
        List<StringTag> itemsTags = new ArrayList<>();
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                Ingredient ingredient = recipe.getIngredient(x, y);
                ItemStackSnapshot item = ingredient.displayedItems().isEmpty() ? ItemStack.empty().createSnapshot() : ingredient.displayedItems().get(0);
                itemsTags.add(new StringTag("ingredient", SerializationUtil.serialize(item.createStack())));
            }
        }
        map.put(new ListTag<>("ingredients", StringTag.class, itemsTags));
    }

    private void loadShapeless(CompoundMap map) {
        @SuppressWarnings("unchecked")
        List<StringTag> itemsTags = (List<StringTag>) map.get("ingredients").getValue();
        ShapelessCraftingRecipe.Builder recipeBuilder = ShapelessCraftingRecipe.builder();
        for(StringTag itemTag : itemsTags) {
            recipeBuilder.addIngredient(Ingredient.builder()
                    .with(SerializationUtil.deserialize(itemTag.getValue()))
                    .build());
        }

        ShapelessCraftingRecipe finalRecipe = ((ShapelessCraftingRecipe.Builder.ResultStep) recipeBuilder)
                .result(Objects.requireNonNull(SerializationUtil.deserialize((String) map.get("result").getValue())))
                .id((String) map.get("id").getValue())
                .build();

        addRecipe(finalRecipe);
    }

    private void loadShaped(CompoundMap map) {
        @SuppressWarnings("unchecked")
        List<StringTag> itemsTags = (List<StringTag>) map.get("ingredients").getValue();
        ShapedCraftingRecipe.Builder.AisleStep recipeBuilder = ShapedCraftingRecipe.builder().aisle("012", "345", "678");
        int i = 0;
        for(StringTag itemTag : itemsTags) {
            recipeBuilder.where(Character.forDigit(i, 10), Ingredient.builder()
                    .with(SerializationUtil.deserialize(itemTag.getValue()))
                    .build());
            i++;
        }
        ShapedCraftingRecipe finalRecipe = ((ShapedCraftingRecipe.Builder.ResultStep) recipeBuilder)
                .result(Objects.requireNonNull(SerializationUtil.deserialize((String) map.get("result").getValue())))
                .id((String) map.get("id").getValue())
                .build();
        addRecipe(finalRecipe);
    }

    @Override
    public void inject(GameRegistry gameRegistry) {
        for(CraftingRecipe recipe : getRecipes()) gameRegistry.getCraftingRecipeRegistry().register(recipe);
    }
}
