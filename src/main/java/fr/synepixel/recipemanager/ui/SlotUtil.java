package fr.synepixel.recipemanager.ui;

import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;

public class SlotUtil {

    public static SlotPos toPos(SlotIndex index, int width){
        if(index.getValue() == null) return SlotPos.of(0, 0);
        return SlotPos.of(index.getValue()%width, index.getValue()/width);
    }

    public static SlotPos toPos(SlotIndex index){
        return toPos(index, 9);
    }
}
