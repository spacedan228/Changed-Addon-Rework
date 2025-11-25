package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ChangedAddonDamageSources {

    public static final DamageHolder LATEX_SOLVENT = holder("latex_solvent");
    public static final DamageHolder CONSCIENCE_LOSE = holder("conscience_lose");
    public static final DamageHolder UNTRANSFUR_FAIL = holder("untransfur_fail");//TODO bypassArmor()

    public record DamageHolder(ResourceKey<DamageType> key) {
        public DamageSource source(Level level) {
            final Holder<DamageType> type = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
            return new DamageSource(type);
        }

        public DamageSource source(RegistryAccess access, Entity sourceEntity) {
            final Holder<DamageType> type = access.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
            return new DamageSource(type, sourceEntity);
        }
    }

    private static DamageHolder holder(String name) {
        return new DamageHolder(ResourceKey.create(Registries.DAMAGE_TYPE, ChangedAddonMod.resourceLoc(name)));
    }

}
