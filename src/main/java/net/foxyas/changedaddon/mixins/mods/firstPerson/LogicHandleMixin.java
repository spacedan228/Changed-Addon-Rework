package net.foxyas.changedaddon.mixins.mods.firstPerson;

import dev.tr7zw.firstperson.LogicHandler;
import net.foxyas.changedaddon.entity.advanced.LatexSnepEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogicHandler.class)
@RequiredMods("firstpersonmod")
public abstract class LogicHandleMixin {

    @Shadow
    private Vec3 offset;
    @Shadow
    @Final
    private Minecraft client;

    @Inject(
            method = "updatePositionOffset",
            at = @At("TAIL")
    )
    private void changedaddon$reuseBodyOffset(Entity entity, float delta, CallbackInfo ci) {
        if (entity != client.player) return;
        if (!isSnep(client.player)) return;
        this.offset = this.offset.multiply(0.8f, 0.8f, 0.8f);
    }


    @Unique
    private boolean isSnep(Entity entity) {
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(entity))
                .map(variant -> variant.getChangedEntity() instanceof LatexSnepEntity)
                .orElse(false);
    }
}