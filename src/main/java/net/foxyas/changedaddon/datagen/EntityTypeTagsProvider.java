package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.foxyas.changedaddon.init.ChangedAddonEntities.*;
import static net.ltxprogrammer.changed.init.ChangedEntities.*;

public class EntityTypeTagsProvider extends net.minecraft.data.tags.EntityTypeTagsProvider {

    public EntityTypeTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ChangedAddonMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ChangedTags.EntityTypes.HUMANOIDS).add(
                ERIK.get());

        tag(ChangedTags.EntityTypes.LATEX).add(LatexEntities.stream().map(Supplier::get)
                .sorted(Comparator.comparing(entityType -> entityType.getRegistryName().getPath()))
                .toList().toArray(new EntityType[0]));

        tag(ChangedTags.EntityTypes.ORGANIC_LATEX).add(
                BUNY.get(),
                MIRROR_WHITE_TIGER.get(),
                REYN.get());
        tag(ChangedTags.EntityTypes.PARTIAL_LATEX).add(
                SNOW_LEOPARD_PARTIAL.get(),
                LATEX_SNEP.get());

        tag(ChangedAddonTags.EntityTypes.CAN_CARRY).add(
                EntityType.WANDERING_TRADER,
                DARK_LATEX_WOLF_PUP.get(),
                EntityType.WOLF);

        tag(ChangedAddonTags.EntityTypes.PATABLE).add(
                EntityType.OCELOT,
                EntityType.PARROT,
                EntityType.CAT,
                EntityType.RABBIT,
                EntityType.WOLF,
                EntityType.FOX,
                LATEX_SNOW_FOX_FOXYAS.get(),
                PROTOTYPE.get(),
                ERIK.get());

        tag(ChangedAddonTags.EntityTypes.DRAGON_ENTITIES).add(
                getCanGlideEntitiesArray()
        );

        tag(ChangedAddonTags.EntityTypes.BEE_ENTITIES).add(
                getBeeEntities()
        );

        tag(ChangedAddonTags.EntityTypes.PACIFY_HANDLE_IMMUNE)
                .add(EXPERIMENT_009.get())
                .add(EXPERIMENT_10.get())
                .add(EXPERIMENT_10_BOSS.get())
                .add(EXPERIMENT_009_BOSS.get());

        tag(ChangedAddonTags.EntityTypes.PACIFY_IMMUNE);

        tag(EntityTypeTags.IMPACT_PROJECTILES).add(
                PARTICLE_PROJECTILE.get(), WITHER_PARTICLE_PROJECTILE.get());

        tag(ChangedAddonTags.EntityTypes.CAN_USE_ACCESSORIES).add(
                ChangedAddonEntities.canUseAccessories().toArray(new EntityType[0])
        );

        tag(ChangedAddonTags.EntityTypes.HAS_CLAWS).add(VOID_FOX.get());
        tag(ChangedAddonTags.EntityTypes.CANT_SPAWN_AS_ALPHA_ENTITY).addTag(ChangedTags.EntityTypes.PUDDING).add(DARK_LATEX_WOLF_PUP.get(), PURE_WHITE_LATEX_WOLF_PUP.get());


        tag(ChangedTags.EntityTypes.CAN_WEAR_EXOSKELETON).add(canUseExoskeleton().toArray(new EntityType[0]));
    }

    private static EntityType<?>[] getCanGlideEntitiesArray() {
        List<EntityType<?>> canGlideEntities = new ArrayList<>();
        Collection<TransfurVariant<?>> transfurVariants = ChangedRegistry.TRANSFUR_VARIANT.get().getValues();
        transfurVariants.forEach((transfurVariant) -> {
            if (transfurVariant.canGlide) {
                canGlideEntities.add(transfurVariant.getEntityType());
            }
        });

        return canGlideEntities.toArray(new EntityType[0]);
    }

    private static EntityType<?>[] getBeeEntities() {
        Stream<EntityType<?>> beeEntities = ForgeRegistries.ENTITIES.getValues().stream().filter((entityType) -> entityType.getRegistryName().toString().contains("latex_bee"));
        List<EntityType<?>> canGlideEntities = new ArrayList<>(beeEntities.toList());
        return canGlideEntities.toArray(new EntityType[0]);
    }
}
