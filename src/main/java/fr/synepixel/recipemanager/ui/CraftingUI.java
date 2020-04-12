package fr.synepixel.recipemanager.ui;

import fr.synepixel.recipemanager.Main;
import fr.synepixel.recipemanager.recipe.CustomRecipeRegistry;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.Identifiable;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CraftingUI extends RecipeUI<CraftingRecipe> {

    private Main plugin;
    private AtomicBoolean shapedRecipe = new AtomicBoolean(true);

    private final ItemStack shaped = ItemStack.builder()
            .itemType(ItemTypes.STAINED_GLASS_PANE)
            .add(Keys.DISPLAY_NAME, Text.builder("Shaped").color(TextColors.GREEN).build())
            .add(Keys.DYE_COLOR, DyeColors.GREEN)
            .build();

    private final ItemStack shapeless = ItemStack.builder()
            .itemType(ItemTypes.STAINED_GLASS_PANE)
            .add(Keys.DISPLAY_NAME, Text.builder("Shapeless").color(TextColors.RED).build())
            .add(Keys.DYE_COLOR, DyeColors.RED)
            .build();

    public CraftingUI(Main plugin, Player player) {
        super(player,
                CustomRecipeRegistry.getRegistry("crafting"),
                Inventory.builder()
                        .property("uid", new Identifiable(UUID.randomUUID()))
                        .build(plugin),
                player.getInventory().first());
        this.plugin = plugin;
    }

    @Override
    public RecipeUI<CraftingRecipe> generateButtons() {
        fillBackground(SlotPos.of(3, 0), SlotPos.of(8, 2));
        removeTopButton(SlotPos.of(4, 1));
        setTopItem(SlotPos.of(4, 1), ItemStack.empty());

        setTopButton(SlotPos.of(7, 2), shaped, event -> {
            shapedRecipe.set(!shapedRecipe.get());
            Task.builder().delayTicks(1).execute(this::updateShapeItem).submit(plugin);
            return true;
        });
        return super.generateButtons();
    }

    @Override
    public boolean onSlotClick(ClickInventoryEvent event, SlotIndex index) {
        if(index.getValue() != 13) return super.onSlotClick(event, index);
        updateRecipes(event.getCursorTransaction().getOriginal().createStack());
        return super.onSlotClick(event, index);
    }

    @Override
    public void clearRecipe() {
        for(int i = 0; i < 9; i++) setTopItem(SlotUtil.toPos(SlotIndex.of(i), 3), ItemStack.empty());
    }

    private void updateShapeItem() {
        setTopItem(SlotPos.of(7, 2), shapedRecipe.get() ? shaped : shapeless);
    }

    @Override
    public void renderRecipe() {

        if(currentRecipe() != null) System.out.println(currentRecipe().getClass());

        if(currentRecipe() instanceof ShapelessCraftingRecipe) {
            shapedRecipe.set(true);
            updateShapeItem();
            ShapelessCraftingRecipe recipe = (ShapelessCraftingRecipe) currentRecipe();
            for(int i = 0; i < recipe.getIngredientPredicates().size(); i++) {
                List<ItemStackSnapshot> displayed = recipe.getIngredientPredicates().get(i).displayedItems();
                setTopItem(SlotUtil.toPos(SlotIndex.of(i), 3), displayed.isEmpty() ? ItemStack.empty() : displayed.get(0).createStack());
            }
        } else if(currentRecipe() instanceof ShapedCraftingRecipe) {
            shapedRecipe.set(false);
            updateShapeItem();
            ShapedCraftingRecipe recipe = (ShapedCraftingRecipe) currentRecipe();
            for(int x = 0; x < recipe.getWidth(); x++) {
                for(int y = 0; y < recipe.getHeight(); y++) {
                    List<ItemStackSnapshot> displayed = recipe.getIngredient(x, y).displayedItems();
                    setTopItem(SlotPos.of(x, y), displayed.isEmpty() ? ItemStack.empty() : displayed.get(0).createStack());
                }
            }
        }
    }

    @Override
    public void add() {
        System.out.println(shapedRecipe.get());
        if(shapedRecipe.get()) {

            ShapedCraftingRecipe.Builder.AisleStep recipeBuilder = ShapedCraftingRecipe.builder()
                .aisle("012", "345", "678");

            for(int i = 0; i < 9; i++){
                Optional<ItemStack> item = getTopSlot(SlotUtil.toPos(SlotIndex.of(i), 3)).peek();
                recipeBuilder.where(Character.forDigit(i, 10), Ingredient.builder().with(item.orElse(ItemStack.empty())).build());
            }

            ShapedCraftingRecipe recipe = ((ShapedCraftingRecipe.Builder.ResultStep)recipeBuilder)
                    .result(getTopSlot(SlotPos.of(4, 1)).peek().get())
                    .id(UUID.randomUUID().toString())
                    .build();

            recipeRegistry.addRecipe(recipe);
        } else {
            ShapelessCraftingRecipe.Builder recipeBuilder = ShapelessCraftingRecipe.builder();
            for(int i = 0; i < 9; i++) {
                Optional<ItemStack> itemStack = getTopSlot(SlotUtil.toPos(SlotIndex.of(i), 3)).peek();
                itemStack.ifPresent(item -> recipeBuilder.addIngredient(Ingredient.builder().with(item).build()));
            }
            recipeRegistry.addRecipe(((ShapelessCraftingRecipe.Builder.ResultStep) recipeBuilder).result(getTopSlot(SlotPos.of(4, 1)).peek().get()).id(UUID.randomUUID().toString()).build());
        }
    }

    private Map<Character, Ingredient> itemMapToIngredient(Map<Character, ItemStack> itemMap) {
        Map<Character, Ingredient> ingredientMap = new HashMap<>();
        for(Map.Entry<Character, ItemStack> entry : itemMap.entrySet()) {
            ingredientMap.put(entry.getKey(), Ingredient.builder().with(entry.getValue()).build());
        }
        return ingredientMap;
    }
}
