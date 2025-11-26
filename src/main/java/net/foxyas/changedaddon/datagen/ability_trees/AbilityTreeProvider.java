package net.foxyas.changedaddon.datagen.ability_trees;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.AbilityCounter;
import net.ltxprogrammer.changed.ability.tree.AbilityTree;
import net.ltxprogrammer.changed.ability.tree.AttributeModifierNodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.StandingOnCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAbilityTreeCodecs;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AbilityTreeProvider implements DataProvider {
    private final PackOutput output;
    private final String modid;

    public AbilityTreeProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    private void generateTrees(Map<ResourceLocation, AbilityTree> trees) {

        // Exemplo: aplicar a transfur feline_light
        List<RegistryElementPredicate<TransfurVariant<?>>> variants =
                List.of(RegistryElementPredicate.forTag(ChangedRegistry.TRANSFUR_VARIANT.get(),
                        Changed.modResource("feline_light")));

        // Criar os nós
        AbilityTree.Node root = new AbilityTree.Node(
                AbilityTree.ROOT_NAME,             // parent
                List.of(),                         // occludes
                "ability.changed.root",            // titleId
                "ability.changed.root.desc",       // descriptionId
                0,                                  // price
                0,                                  // group discount
                List.of(),                         // acquiredEffects
                List.of()                          // missingEffects
        );

        AbilityTree.Node claws = new AbilityTree.Node(
                Changed.modResource("root"),
                List.of(),
                "ability.changed.claws",
                "ability.changed.claws.desc",
                5,
                0,
                List.of(new AttributeModifierNodeEffect(
                        new StandingOnCondition(List.of(RegistryElementPredicate.forID(ForgeRegistries.BLOCKS, ForgeRegistries.BLOCKS.getKey(Blocks.GRASS_BLOCK)))),
                                ForgeMod.SWIM_SPEED.get(), 1
                        ),
                        new AttributeModifierNodeEffect(TrueCondition.INSTANCE, Attributes.ATTACK_DAMAGE, 0.5)
                )
                ,
                List.of()
        );

        Map<ResourceLocation, AbilityTree.Node> nodes = new HashMap<>();
        nodes.put(Changed.modResource("root"), root);
        nodes.put(Changed.modResource("claws"), claws);

        AbilityTree tree = new AbilityTree(variants, nodes);
        tree.setTreeLocation(Changed.modResource("feline"));

        trees.put(tree.getTreeLocation(), tree);
    }

    //Todo make a kind of builder like class instead of raw constructors


    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceLocation, AbilityTree> trees = new HashMap<>();

        generateTrees(trees);

        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (var entry : trees.entrySet()) {
            ResourceLocation id = entry.getKey();
            AbilityTree tree = entry.getValue();

            Path path = output.getOutputFolder()
                    .resolve("data/" + modid + "/ability_trees/" + id.getPath() + ".json");

            JsonElement json = AbilityTree.CODEC.encodeStart(JsonOps.INSTANCE, tree)
                    .result()
                    .orElseThrow(() -> new IllegalStateException("Failed to encode AbilityTree: " + id));

            futures.add(DataProvider.saveStable(cache, json, path));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Ability Tree Provider";
    }
}
