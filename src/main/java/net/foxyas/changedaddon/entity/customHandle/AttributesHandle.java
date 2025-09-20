package net.foxyas.changedaddon.entity.customHandle;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

public class AttributesHandle {

    public static AttributeMap DefaultPlayerAttributes() {
        return new AttributeMap(Player.createAttributes().build());
    }

    public static final float DefaultPlayerFlySpeed = 0.05F;
}
