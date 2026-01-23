package net.foxyas.changedaddon.init;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;

public class ChangedAddonDamageSources {

    public static final DamageSource LATEX_SOLVENT = new DamageSource("latex_solvent");
    public static final DamageSource CONSCIENCE_LOSE = new DamageSource("conscience_lose").bypassArmor();

    public static EntityDamageSource mobLatesSolventAttack(LivingEntity mob) {
        return new EntityDamageSource("latex_solvent", mob);
    }

    /*
     * For The Incoming Port this will be helpful
     * Todo in 1.20.1: DamageTypeProvider DataProvider
     public record DamageHolder(ResourceKey<DamageType> key) {
        public DamageSource source(RegistryAccess access) {
            final Holder<DamageType> type = access.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
            return new DamageSource(type);
        }

        public DamageSource source(RegistryAccess access, Entity sourceEntity) {
            final Holder<DamageType> type = access.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
            return new DamageSource(type, sourceEntity);
        }
    }

    private static DamageHolder holder(String name) {
        return new DamageHolder(ResourceKey.create(Registries.DAMAGE_TYPE, ChangedAdditionsMod.modResource(name)));
    }

    public static final DamageHolder LATEX_SOLVENT = holder("latex_solvent");
    */
}
