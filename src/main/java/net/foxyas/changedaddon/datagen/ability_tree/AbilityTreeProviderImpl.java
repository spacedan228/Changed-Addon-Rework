package net.foxyas.changedaddon.datagen.ability_tree;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.AbilityTree;
import net.ltxprogrammer.changed.ability.tree.AttributeModifierNodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.StandingOnCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class AbilityTreeProviderImpl extends AbilityTreeProvider {

    public AbilityTreeProviderImpl(PackOutput output) {
        super(output, ChangedAddonMod.MODID);
    }

    @Override
    protected void addTrees() {
//        addTree(Changed.modResource("feline"), List.of(RegistryElementPredicate.forTag(ChangedRegistry.TRANSFUR_VARIANT.get(),
//                Changed.modResource("feline_light"))))
//                .withNode(Changed.modResource("root"), new AbilityTree.Node(
//                        AbilityTree.ROOT_NAME,             // parent
//                        List.of(),                         // occludes
//                        "ability.changed.root",            // titleId
//                        "ability.changed.root.desc",       // descriptionId
//                        0,                                 // price
//                        0,                                 // group discount
//                        List.of(),                         // acquiredEffects
//                        List.of()                          // missingEffects
//                ))
//                .withNode(Changed.modResource("claws"), new AbilityTree.Node(
//                        AbilityTree.ROOT_NAME,
//                        List.of(),
//                        "ability.changed.claws",
//                        "ability.changed.claws.desc",
//                        5,
//                        0,
//                        List.of(new AttributeModifierNodeEffect(
//                                        new StandingOnCondition(List.of(RegistryElementPredicate.forID(ForgeRegistries.BLOCKS, ForgeRegistries.BLOCKS.getKey(Blocks.GRASS_BLOCK)))),
//                                        ForgeMod.SWIM_SPEED.get(), 1
//                                ),
//                                new AttributeModifierNodeEffect(TrueCondition.INSTANCE, Attributes.ATTACK_DAMAGE, 0.5)
//                        )
//                        ,
//                        List.of()
//                ));
    }
}
