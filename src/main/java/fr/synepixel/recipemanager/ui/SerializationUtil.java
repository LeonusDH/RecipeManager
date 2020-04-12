package fr.synepixel.recipemanager.ui;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;

public class SerializationUtil {

    private static final String SEPARATOR = "#-#";

    /*@SuppressWarnings("rawtypes")
    public static ListTag<?> serializeSponge(String name, DataContainer container) {
        Set<DataQuery> keys = container.getKeys(false);
        List<Tag> tags = new ArrayList<>();
        for(DataQuery key : keys){
            tags.add(unsafeWrap(key.asString(SEPARATOR), tags));
        }
        return new ListTag<>(name, Tag.class, tags);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Tag<T> unsafeWrap(String name, T value) {
        Class<T> clazz = (Class<T>) value.getClass();
        if(value instanceof Byte) {
            return (Tag<T>) new ByteTag(name, (byte)value);
        } else if(value instanceof Short) {
            return (Tag<T>) new ShortTag(name, (short)value);
        } else if(value instanceof Integer) {
            return (Tag<T>) new IntTag(name, (int)value);
        } else if(value instanceof Long) {
            return (Tag<T>) new LongTag(name, (long)value);
        } else if(value instanceof Float) {
            return (Tag<T>) new FloatTag(name, (float)value);
        } else if(value instanceof Double) {
            return (Tag<T>) new DoubleTag(name, (double)value);
        } else if(value instanceof String) {
            return (Tag<T>) new StringTag(name, (String) value);
        } else if(value instanceof List<?>) {
            List<Tag> tags = new ArrayList<>();
            for(Object v : (List<?>)value){
                Tag<?> tag = unsafeWrap("element", v);
                if(tag != null) tags.add(tag);
            }
            return (Tag<T>) new ListTag<>(name, Tag.class, tags);
        } else if(value instanceof Map<?, ?>) {
            try {
                Map<String, ?> map = (Map<String, ?>) value;
                CompoundMap compoundMap = new CompoundMap();
                for(Map.Entry<String, ?> entry : map.entrySet()){
                    Tag<?> tag = unsafeWrap("entry", entry.getValue());
                    if(tag != null) compoundMap.put(tag);
                }
                return (Tag<T>) new CompoundTag(name, compoundMap);
            } catch(ClassCastException e){
                return (Tag<T>) new CompoundTag(name, new CompoundMap());
            }
        }
        return null;
    }*/

    public static String serialize(ItemStack itemStack) {
        try {
            StringWriter sink = new StringWriter();
            GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
            ConfigurationNode node = loader.createEmptyNode();
            node.setValue(TypeToken.of(ItemStack.class), itemStack);
            loader.save(node);
            return sink.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack deserialize(String item) {
        try {
            StringReader source = new StringReader(item);
            GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
            ConfigurationNode node = loader.load();
            return node.getValue(TypeToken.of(ItemStack.class));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
