package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.client.renderer.layers.features.RenderMode;
import net.foxyas.changedaddon.network.packets.ClientboundSonarUpdatePacket;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class TestAbility extends SimpleAbility {

    public TestAbility() {
        super();
    }


    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);

        if (entity.getEntity() instanceof ServerPlayer serverPlayer) {
            ClientboundSonarUpdatePacket.update(serverPlayer, 200, 10, 10, 16, RenderMode.SONAR);
        }
    }
}
