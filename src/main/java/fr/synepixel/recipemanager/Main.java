package fr.synepixel.recipemanager;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.google.inject.Inject;
import fr.synepixel.recipemanager.command.CommandCraft;
import fr.synepixel.recipemanager.recipe.CraftingRecipeRegistry;
import fr.synepixel.recipemanager.recipe.CustomRecipeRegistry;
import fr.synepixel.recipemanager.recipe.RecipeType;
import fr.synepixel.recipemanager.ui.UIEventListener;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Plugin(id = "recipemanager", dependencies = @Dependency(id = "boxboy", version = "1.3"))
public class Main {
    @Inject
    private Logger logger;

    @Listener
    public void onPostInit(GamePostInitializationEvent event) throws IOException {
        CustomRecipeRegistry.addRegistry(new CraftingRecipeRegistry());

        logger.info("Loading recipes...");
        int count = 0;
        NBTInputStream stream = new NBTInputStream(new FileInputStream(getSaveFile()));
        @SuppressWarnings("unchecked")
        List<CompoundTag> registries = (List<CompoundTag>) ((CompoundTag)stream.readTag()).getValue().get("registries").getValue();
        for(CompoundTag registryData : registries){
            CustomRecipeRegistry<? extends Recipe> registry = CustomRecipeRegistry.getRegistry((String) registryData.getValue().get("key").getValue());
            if(registry == null){
                logger.warn("Skipping unknown registry: " + registryData.getValue().get("key").getValue());
                continue;
            }
            registry.load(registryData.getValue());
            count+=registry.getRecipes().size();
        }
        logger.info("Loaded " + count + " recipes");

        logger.info("Registering recipes");
        for(CustomRecipeRegistry<? extends Recipe> registry : CustomRecipeRegistry.getRegistries()) registry.inject(Sponge.getRegistry());
    }

    @Listener
    public void onServerStart(GameStartingServerEvent event) {

        CommandSpec cmd = CommandSpec.builder()
                .executor(new CommandCraft(this))
                .permission("craft.use")
                .description(Text.of("Open recipe manager"))
                .arguments(GenericArguments.choices(Text.of("type"), RecipeType.getMap())).build();
        Sponge.getCommandManager().register(this, cmd, "craft");

        Sponge.getEventManager().registerListeners(this, new UIEventListener());
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) throws IOException {
        logger.info("Saving recipes...");
        List<CompoundTag> registries = new ArrayList<>();
        for(CustomRecipeRegistry<? extends Recipe> registry : CustomRecipeRegistry.getRegistries()) {
            logger.info("Saving " + registry.getRecipes().size() + " recipes of the registry " + registry.getKey());
            CompoundMap map = new CompoundMap();
            registry.save(map);
            map.put("key", new StringTag("key", registry.getKey()));
            registries.add(new CompoundTag(registry.getKey(), map));
        }
        ListTag<CompoundTag> finalTag = new ListTag<>("registries", CompoundTag.class, registries);


        NBTOutputStream stream = new NBTOutputStream(new FileOutputStream(getSaveFile()));
        CompoundMap map = new CompoundMap(); //Make the final file compatible with NBTExplorer
        map.put("recipes", finalTag);
        stream.writeTag(new CompoundTag("data", map));
        stream.close();
    }


    private File getSaveFile() throws IOException {
        File dir = new File("./mods/plugins/RecipeManager");
        File saveFile = new File(dir, "save.dat");
        if(!dir.exists()) dir.mkdirs();
        if(!saveFile.exists()) saveFile.createNewFile();
        return saveFile;
    }
}
