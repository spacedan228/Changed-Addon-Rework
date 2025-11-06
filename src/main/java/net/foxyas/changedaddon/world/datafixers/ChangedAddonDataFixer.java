package net.foxyas.changedaddon.world.datafixers;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonEnchantments;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ChangedAddonDataFixer {
    public static final int DATAFIX_ID = 1; // used to internal version
    private static final Map<DataFixTypes, Consumer<CompoundTag>> DATA_FIXERS = new HashMap<>();

    // Data Fixers
    private final Map<ResourceLocation, ResourceLocation> ENCHANTMENT_REMAP = new HashMap<>();


    // Future Data fixers
    private final Map<ResourceLocation, ResourceLocation> ENTITY_ID_REMAP = Util.make(new HashMap<>(), map -> {});
    private final Map<ResourceLocation, ResourceLocation> ITEM_ID_REMAP   = Util.make(new HashMap<>(), map -> {});
    private final Map<ResourceLocation, ResourceLocation> BLOCK_ID_REMAP  = Util.make(new HashMap<>(), map -> {});
    private final Map<ResourceLocation, ResourceLocation> BLOCK_ITEM_ID_REMAP = Util.make(new HashMap<>(), map -> {});
    private final Map<ResourceLocation, ResourceLocation> VARIANT_ID_REMAP = Util.make(new HashMap<>(), map -> {});
    private final Map<String, String> ENUM_REMAP = Util.make(new HashMap<>(), map -> {});
    private final Map<String, String> TAG_REMAP  = Util.make(new HashMap<>(), map -> {});

    private static final Consumer<CompoundTag> NULL_OP = (tag) -> {};

    public ChangedAddonDataFixer() {
        // define remapeamentos de IDs antigos para novos
        ENCHANTMENT_REMAP.put(
                ResourceLocation.parse(ChangedAddonMod.resourceLocString("solvent")),
                ChangedAddonEnchantments.LATEX_SOLVENT.getId()
        );

        // adiciona um tipo de fix (aqui corrigimos inventário de player)
        DATA_FIXERS.put(DataFixTypes.PLAYER, this::fixPlayerData);
    }

    private void fixPlayerData(CompoundTag tag) {
        this.updateTagNames(tag);
        if (tag.contains("Inventory")) {
            ListTag inv = tag.getList("Inventory", 10);
            inv.forEach(i -> {
                if (i instanceof CompoundTag itemTag) {
                    this.updateItem(itemTag);
                }
            });
        }
    }

    private void fixEnchantments(CompoundTag tag) {
        if (!tag.contains("Enchantments", 9)) // 10 = tipo ListTag
            return;

        ListTag enchantments = tag.getList("Enchantments", 10); // 10 = CompoundTag
        for (int i = 0; i < enchantments.size(); i++) {
            CompoundTag ench = enchantments.getCompound(i);
            if (!ench.contains("id")) continue;

            ResourceLocation oldId = ResourceLocation.tryParse(ench.getString("id"));
            if (oldId == null) continue;

            if (ENCHANTMENT_REMAP.containsKey(oldId)) {
                ResourceLocation newId = ENCHANTMENT_REMAP.get(oldId);
                ChangedAddonMod.LOGGER.info("Remapping enchantment {} → {}", oldId, newId);
                ench.putString("id", newId.toString());
            }
        }
    }

    private void updateTagNames(@NotNull CompoundTag tag) {
        tag.getAllKeys().stream().toList().forEach((key) -> {
            if (this.TAG_REMAP.containsKey(key)) {
                String newKey = this.TAG_REMAP.get(key);
                Tag subTag = tag.get(key);
                if (subTag != null && !newKey.equals(key)) {
                    tag.put(newKey, subTag);
                    tag.remove(key);
                }
            }

        });
    }

    private void updateID(@NotNull Map<ResourceLocation, ResourceLocation> remap, @NotNull CompoundTag tag, String idName) {
        if (tag.contains(idName)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString(idName));
            if (id != null) {
                tag.putString(idName, this.updateID(remap, id).toString());
            }

        }
    }

    private void updateName(@NotNull Map<String, String> remap, @NotNull CompoundTag tag, String idName) {
        if (tag.contains(idName)) {
            String id = tag.getString(idName);
            tag.putString(idName, this.updateName(remap, id));
        }
    }

    private void updateItem(@NotNull CompoundTag itemStack) {
        this.updateID(this.ITEM_ID_REMAP, itemStack, "id");
        this.updateID(this.BLOCK_ITEM_ID_REMAP, itemStack, "id");
        if (itemStack.contains("tag")) {
            this.updateItemTag(itemStack.getCompound("tag"));
        }

    }

    private void updateItemTag(@NotNull CompoundTag itemTag) {
        this.updateID(this.VARIANT_ID_REMAP, itemTag, "form");

        this.fixEnchantments(itemTag);
    }

    /**
     * Aplica correção dependendo do tipo
     */
    public void updateCompoundTag(@NotNull DataFixTypes type, @Nullable CompoundTag tag) {
        if (tag != null) {
            DATA_FIXERS.getOrDefault(type, NULL_OP).accept(tag);
        }
    }

    // Helpers
    private ResourceLocation updateID(@NotNull Map<ResourceLocation, ResourceLocation> remap, ResourceLocation id) {
        return remap.getOrDefault(id, id);
    }

    private String updateName(@NotNull Map<String, String> remap, String name) {
        return remap.getOrDefault(name, name);
    }
}
