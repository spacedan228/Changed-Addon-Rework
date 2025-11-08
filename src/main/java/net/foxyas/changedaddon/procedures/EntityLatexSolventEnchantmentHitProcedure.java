package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonAttributes;
import net.foxyas.changedaddon.init.ChangedAddonParticleTypes;
import net.foxyas.changedaddon.mixins.entity.attributes.AttributeMapAccessor;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityLatexSolventEnchantmentHitProcedure {

    @SubscribeEvent
    public static void onEntityAttacked(LivingHurtEvent event) {
        Entity target = event.getEntity();
        Entity directEntity = event.getSource().getDirectEntity();

        if (target == null || directEntity == null) {
            return;
        }

        // Verifica o nível do encantamento "Solvent"
        double latexSolventLevel = getLatexSolventLevelOfEntity(directEntity);
        if (latexSolventLevel <= 0) {
            return;
        }

        // Verifica se a entidade deve ser afetada
        if (shouldAffectEntity(target) && isValidWeapon(directEntity)) {
            applyEffects(event, target, (float) latexSolventLevel);
        }
    }

    private static double getAttributeValueSafe(LivingEntity livingEntity, Attribute attribute) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            return attributeInstance.getValue();
        }
        return 0;
    }

    private static double getLatexSolventLevelOfEntity(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack mainHandItem = livingEntity.getMainHandItem();
            //int itemEnchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(ChangedAddonEnchantments.LATEX_SOLVENT.get(), mainHandItem);
            return getAttributeValueSafe(livingEntity, ChangedAddonAttributes.LATEX_SOLVENT_DAMAGE_MULTIPLIER.get());
        } else if (entity instanceof ThrownTrident trident) {
            // Pega o dono (se existir)
            Entity ownerEntity = trident.getOwner();
            if (ownerEntity instanceof LivingEntity owner) {
                // Cria um mapa temporário com cópia dos atributos atuais
                AttributeMap tempMap;
                if (owner.getAttributes() instanceof AttributeMapAccessor attributeMapAccessor) {
                    tempMap = new AttributeMap(attributeMapAccessor.getSupplier());
                    tempMap.assignValues(owner.getAttributes());
                } else {
                    tempMap = owner.getAttributes();
                }

                // Copia o item do tridente
                CompoundTag tag = new CompoundTag();
                trident.save(tag);
                ItemStack tridentItem = tag.contains("Trident")
                        ? ItemStack.of(tag.getCompound("Trident"))
                        : new ItemStack(Items.TRIDENT);

                // Aplica os modifiers do item no mapa temporário
                tridentItem.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((attr, modifier) -> {
                    if (attr == ChangedAddonAttributes.LATEX_SOLVENT_DAMAGE_MULTIPLIER.get()) {
                        AttributeInstance instance = tempMap.getInstance(attr);
                        if (instance != null) {
                            instance.addTransientModifier(modifier);
                        }
                    }
                });

                // Agora lê o valor total desse atributo simulado
                return tempMap.getValue(ChangedAddonAttributes.LATEX_SOLVENT_DAMAGE_MULTIPLIER.get());
            }
        }
        return 0;
    }

    private static boolean shouldAffectEntity(Entity entity) {
        return entity instanceof Player player && ProcessTransfur.isPlayerLatex(player)
                || (entity.getType().is(ChangedTags.EntityTypes.LATEX)
                && entity instanceof ChangedEntity);
    }

    private static boolean isValidWeapon(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack mainHandItem = livingEntity.getMainHandItem();
            return !(mainHandItem.getItem() instanceof BowItem || mainHandItem.getItem() instanceof CrossbowItem);
        }
        return entity instanceof ThrownTrident || !(entity instanceof Projectile);
    }

    private static void applyEffects(LivingHurtEvent event, Entity target, float attributeValue) {
        // Multiplica o dano baseado no nível do encantamento
        float multiplier = event.getAmount() * calculateDamageMultiplier(attributeValue);
        event.setAmount(multiplier);

        // Aplica som e partículas
        playSoundAndParticles(target);
    }

    private static float calculateDamageMultiplier(float attributeValue) {
        return 1.0f + (attributeValue);
    }

    private static void playSoundAndParticles(Entity entity) {
        Level level = entity.level;

        // Toca som de extinção de fogo
        level.playSound(null, entity, SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 2.5f, 0);

        // Emite partículas
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    (SimpleParticleType) ChangedAddonParticleTypes.SOLVENT_PARTICLE.get(),
                    entity.getX(),
                    entity.getY() + 1,
                    entity.getZ(),
                    10,
                    0.2, 0.3, 0.2, 0.1
            );
        }
    }
}
