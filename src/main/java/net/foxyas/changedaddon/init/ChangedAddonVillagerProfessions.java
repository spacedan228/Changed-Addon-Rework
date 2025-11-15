package net.foxyas.changedaddon.init;

import com.google.common.collect.ImmutableSet;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ChangedAddonVillagerProfessions {

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registry.POINT_OF_INTEREST_TYPE_REGISTRY, ChangedAddonMod.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(Registry.VILLAGER_PROFESSION_REGISTRY, ChangedAddonMod.MODID);

    public static final RegistryObject<VillagerProfession> SCIENTIST = registerProfession("scientist", ChangedAddonBlocks.UNIFUSER, () -> SoundEvents.BREWING_STAND_BREW);

    private static RegistryObject<VillagerProfession> registerProfession(String name, Supplier<? extends Block> block, Supplier<SoundEvent> soundEvent) {
        RegistryObject<PoiType> jobPoi = POI_TYPES.register(name, ()-> new PoiType(name, ImmutableSet.copyOf(block.get().getStateDefinition().getPossibleStates()), 1, 1));
        return PROFESSIONS.register(name, () -> new VillagerProfession(ChangedAddonMod.MODID + ":" + name, jobPoi.get(), ImmutableSet.of(), ImmutableSet.of(), soundEvent.get()));
    }
}
