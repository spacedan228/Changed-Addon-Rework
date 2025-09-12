package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.client.renderer.layers.features.RenderMode;
import net.foxyas.changedaddon.network.PacketUtil;
import net.foxyas.changedaddon.network.packets.ClientboundSonarUpdatePacket;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.ltxprogrammer.changed.entity.VisionType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.Collection;

public class SonarAbility extends SimpleAbility {

    public SonarAbility() {
        super();
    }

    @Override
    public TranslatableComponent getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("changed_addon.ability.sonar");
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        Collection<Component> description = new ArrayList<>(super.getAbilityDescription(entity));
        description.add(new TranslatableComponent("changed_addon.ability.sonar.desc"));
        return description;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 30 * 20;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        if (entity.getEntity() instanceof ServerPlayer serverPlayer) {
            TransfurVariant<?> transfurVariant = entity.getTransfurVariant();
            if (transfurVariant == null) return;

            RenderMode mode = transfurVariant.visionType == VisionType.BLIND
                    ? RenderMode.ECHO_LOCATION
                    : RenderMode.SONAR;

            if (mode == RenderMode.ECHO_LOCATION || transfurVariant.is(ChangedAddonTransfurVariants.VOID_FOX.get())) {
                // Play echo-location sound at player's position
                PacketUtil.playSound(serverPlayer.getLevel(),
                        (sPlayer) -> sPlayer.is(serverPlayer),
                        serverPlayer.position(),
                        SoundEvents.AMETHYST_BLOCK_CHIME,
                        SoundSource.PLAYERS,
                        4.0f, // volume
                        1.0f  // pitch
                );

                /*serverPlayer.getLevel().playSound(
                        null, // null = audible to all players near
                        serverPlayer.blockPosition(),
                        SoundEvents.AMETHYST_BLOCK_CHIME,
                        SoundSource.PLAYERS,
                        4.0f, // volume
                        1.0f  // pitch
                );*/
            }

            ClientboundSonarUpdatePacket.update(serverPlayer, 400, 10, 50, 32, mode);
        }
    }

}
