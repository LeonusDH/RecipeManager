package fr.synepixel.recipemanager.ui;

import fr.synepixel.recipemanager.recipe.CustomRecipeRegistry;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.Identifiable;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class RecipeUI<T extends Recipe> {

    private static Map<UUID, RecipeUI<?>> interfaces = new HashMap<>();

    private Map<SlotPos, Function<ClickInventoryEvent, Boolean>> clickTop = new HashMap<>();
    private Map<SlotPos, Function<ClickInventoryEvent, Boolean>> clickBottom = new HashMap<>();

    private Player player;
    protected CustomRecipeRegistry<T> recipeRegistry;
    private Inventory topInventory;
    private Inventory bottomInventory;
    private ItemStack background = ItemStack.builder()
            .itemType(ItemTypes.STAINED_GLASS_PANE)
            .add(Keys.DYE_COLOR, DyeColors.PURPLE)
            .build();
    protected int recipeIndex = 0;
    private List<T> availableRecipes = new ArrayList<>();

    public RecipeUI(Player player, CustomRecipeRegistry<T> recipeRegistry, Inventory topInventory, Inventory bottomInventory) {
        this.player = player;
        this.recipeRegistry = recipeRegistry;
        this.topInventory = topInventory;
        this.bottomInventory = bottomInventory;
    }

    public RecipeUI<T> generateButtons() {
        ItemStack addButton = ItemStack.builder()
                .itemType(ItemTypes.WOOL)
                .add(Keys.DISPLAY_NAME, Text.builder("Add").color(TextColors.GREEN).build())
                .add(Keys.DYE_COLOR, DyeColors.GREEN)
                .build();

        ItemStack removeButton = ItemStack.builder()
                .itemType(ItemTypes.WOOL)
                .add(Keys.DISPLAY_NAME, Text.builder("Remove").color(TextColors.RED).build())
                .add(Keys.DYE_COLOR, DyeColors.RED)
                .build();

        ItemStack nbtButton = ItemStack.builder()
                .itemType(ItemTypes.DEADBUSH)
                .add(Keys.DISPLAY_NAME, Text.builder("Check NBT").color(TextColors.GOLD).build())
                .build();

        ItemStack previousRecipeButton = ItemStack.builder()
                .itemType(ItemTypes.SKULL)
                .add(Keys.DISPLAY_NAME, Text.builder("Previous recipe").color(TextColors.WHITE).build())
                .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                .add(Keys.REPRESENTED_PLAYER, GameProfile.of(UUID.fromString("a68f0b64-8d14-4000-a95f-4b9ba14f8df9")))
                .build();

        ItemStack nextRecipeButton = ItemStack.builder()
                .itemType(ItemTypes.SKULL)
                .add(Keys.DISPLAY_NAME, Text.builder("Next recipe").color(TextColors.WHITE).build())
                .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                .add(Keys.REPRESENTED_PLAYER, GameProfile.of(UUID.fromString("50c8510b-5ea0-4d60-be9a-7d542d6cd156")))
                .build();

        setTopButton(SlotPos.of(6, 0), addButton, event -> {
            add();
            event.getCause().first(Player.class).get().sendMessage(Text.builder("Added recipe !\nRestart the server to apply changes.").color(TextColors.GREEN).build());
            return true;
        });
        setTopButton(SlotPos.of(8, 0), removeButton, event -> true);
        setTopButton(SlotPos.of(7, 1), nbtButton, event -> true);
        setTopButton(SlotPos.of(6, 2), previousRecipeButton, event -> {
            previousRecipe();
            return true;
        });
        setTopButton(SlotPos.of(8, 2), nextRecipeButton, event -> {
            nextRecipe();
            return true;
        });

        return this;
    }

    public void updateRecipes(ItemStack itemStack) {
        recipeIndex = 0;
        System.out.println(recipeRegistry);
        availableRecipes = new ArrayList<>(recipeRegistry.getRecipes())
                .stream()
                .filter(recipe -> recipe.getExemplaryResult().createStack().equalTo(itemStack))
                .collect(Collectors.toList());
        refreshRecipe();
    }

    protected void fillBackground(SlotPos from, SlotPos to) {
        for(int x = from.getX(); x <= to.getX(); x++) {
            for(int y = from.getY(); y <= to.getY(); y++) {
                setTopButton(SlotPos.of(x, y), background, event -> true);
            }
        }
    }

    public Inventory getTopInventory() {
        return topInventory;
    }

    public Inventory getBottomInventory() {
        return bottomInventory;
    }

    public void open() {
        player.openInventory(topInventory);
        interfaces.put(player.getUniqueId(), this);
    }

    public Slot getTopSlot(SlotPos pos) {
        return topInventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(pos));
    }

    public Slot getBottomSlot(SlotPos pos) {
        return bottomInventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(pos));
    }

    public void setTopItem(SlotPos pos, ItemStack itemStack) {
        getTopSlot(pos).set(itemStack);
    }

    public void setBottomItem(SlotPos pos, ItemStack itemStack) {
        getBottomSlot(pos).set(itemStack);
    }

    public void setTopButton(SlotPos pos, ItemStack itemStack, Function<ClickInventoryEvent, Boolean> consumer) {
        setTopItem(pos, itemStack);
        clickTop.put(pos, consumer);
    }

    public void setBottomButton(SlotPos pos, ItemStack itemStack, Function<ClickInventoryEvent, Boolean> consumer) {
        setBottomItem(pos, itemStack);
        clickBottom.put(pos, consumer);
    }

    public void removeTopButton(SlotPos pos){
        clickTop.remove(pos);
    }

    public Function<ClickInventoryEvent, Boolean> getTopAction(SlotIndex index) {
        return clickTop.getOrDefault(SlotUtil.toPos(index), event -> false);
    }

    public Function<ClickInventoryEvent, Boolean> getBottomAction(SlotIndex index) {
        return clickBottom.getOrDefault(SlotUtil.toPos(index), event -> false);
    }

    public boolean onClick(ClickInventoryEvent event) {
        if(event.getSlot().isPresent()) return onSlotClick(event, event.getSlot().get().getInventoryProperty(SlotIndex.class).get());
        return false;
    }

    public boolean onSlotClick(ClickInventoryEvent event, SlotIndex index){
        if(event.getTargetInventory().getProperty(Identifiable.class, "uid").equals(topInventory.getProperty(Identifiable.class, "uid"))) {
            return getTopAction(index).apply(event);
        } else {
            return getBottomAction(index).apply(event);
        }
    }

    public void refreshRecipe(){
        clearRecipe();
        renderRecipe();
    }

    public abstract void renderRecipe();

    public T currentRecipe(){
        if(availableRecipes.size() == 0) return null;
        return availableRecipes.get(recipeIndex);
    }

    public void nextRecipe(){
        recipeIndex++;
        if(recipeIndex >= availableRecipes.size()) recipeIndex--;
        refreshRecipe();
    }

    public void previousRecipe(){
        recipeIndex--;
        if(recipeIndex < 0) recipeIndex = 0;
        refreshRecipe();
    }

    public abstract void add();

    public abstract void clearRecipe();

    public static RecipeUI<?> getInterface(UUID uuid) {
        return interfaces.get(uuid);
    }
}
