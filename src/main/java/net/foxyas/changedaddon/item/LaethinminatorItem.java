package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonDamageSources;
import net.foxyas.changedaddon.init.ChangedAddonFluids;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.util.ParticlesUtil;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.foxyas.changedaddon.util.FoxyasUtils.getRelativePosition;

public class LaethinminatorItem extends FlamethrowerLike {

    public LaethinminatorItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.CHANGED_ADDON_MAIN_TAB).durability(320).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int ticks) {
        super.onUseTick(level, entity, stack, ticks);
        if (!(entity instanceof Player player)) {
            return;
        }

        if (stack.getDamageValue() >= stack.getMaxDamage() - 1 || entity.isShiftKeyDown()) {
            BlockHitResult hitResult = level.clip(new ClipContext(player.getEyePosition(1.0F), // Posição inicial (olhos do jogador)
                    player.getEyePosition(1.0F).add(player.getLookAngle().scale(5.0)), // Posição final (olhando 5 blocos à frente)
                    ClipContext.Block.OUTLINE, // Modo de colisão com blocos
                    ClipContext.Fluid.ANY, // Considera apenas fontes de fluido
                    player // Entidade que está fazendo o ray trace
            ));
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = level.getBlockState(pos);
                if (state.getFluidState().is(ChangedAddonFluids.LITIX_CAMONIA_FLUID.get()) || state.getFluidState().is(ChangedAddonFluids.FLOWING_LITIX_CAMONIA_FLUID.get())) {
                    stack.setDamageValue(0);
                    entity.playSound(SoundEvents.BUCKET_FILL, 1f, 1f);
                }
            }
            entity.stopUsingItem(); // Stop before breaking
            return;
        }

        if (level.isClientSide)
            return;

        stack.hurtAndBreak(1, entity, (livingEntity) -> {
            livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });

        shoot(player.getLevel(), player, 16, 5, 5);
    }

    @Override
    protected ParticleOptions particle() {
        return ChangedParticles.gas(Color3.fromInt(-1));
    }

    protected void shoot(ServerLevel level, Player player, int maxRange, int horizontalRadius, int verticalRadius) {
        Vec3 eyePosition = player.getEyePosition(1.0F); // Posição dos olhos do jogador
        //Vec3 lookDirection = player.getLookAngle().normalize();    // Direção para onde o jogador está olhando
        //aplicar um efeito de particulas de "gas"
        InteractionHand hand = player.getUsedItemHand();

        ParticleOptions particle = particle();
        for (int i = 1; i <= maxRange; i++) {
            // Calcula a posição do bloco na trajetória do laser
            Vec3 targetVec = eyePosition.add(getRelativePosition(player, 0, 0, i, true));
            BlockPos targetPos = new BlockPos(targetVec);

            double deltaX = hand == InteractionHand.MAIN_HAND ? 0.25 : -0.25;
            if (player.getMainArm() == HumanoidArm.LEFT) deltaX = -deltaX;

            Vec3 relativePosition = getRelativePosition(player, deltaX, 0, i * 0.5 + 1f, true);
            Vec3 maxRelativePosition = getRelativePosition(player, deltaX, 0, maxRange * 0.5, true);
            ParticlesUtil.sendParticlesWithMotionAndOffset(player, particle, player.getEyePosition().add(relativePosition), new Vec3(0.15f, 0.15f, 0.15f), maxRelativePosition, new Vec3(0.25f, 0.25f, 0.25f), 2, 0.10f);

            affectSurroundingEntities(level, player, targetVec, 4 * ((double) i / maxRange));

            // Verifica se o bloco é ar; se for, ignora essa fileira
            if (level.getBlockState(targetPos).isAir()) {
                // Afeta os blocos ao redor do ponto atual
                continue;
            }

            affectSurroundingBlocks(level, targetPos, horizontalRadius, verticalRadius, particle);
        }
    }

    protected void affectSurroundingEntities(ServerLevel level, Player player, Vec3 targetPos, double area) {
        List<ChangedEntity> entityList = level.getEntitiesOfClass(ChangedEntity.class, new AABB(targetPos, targetPos).inflate(area), (changedEntity) -> changedEntity.getType().is(ChangedTags.EntityTypes.LATEX));
        for (ChangedEntity en : entityList) {
            boolean isAllied = player.isAlliedTo(en);
            if (player.canAttack(en) && !isAllied) {
                affectEntity(player, en);
            }
        }
    }

    @Override
    protected void affectEntity(Player shooter, LivingEntity entity) {
        entity.hurt(ChangedAddonDamageSources.mobLatesSolventAttack(shooter), 6);
    }
}
