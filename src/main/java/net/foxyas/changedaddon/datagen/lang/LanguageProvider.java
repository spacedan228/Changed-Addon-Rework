package net.foxyas.changedaddon.datagen.lang;

import net.minecraft.client.KeyMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

//Helper methods from a_changed(mostly*)
public abstract class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {

    protected final String modid;

    public LanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
        this.modid = modid;
    }

    protected void addAdvancement(String key, String title, String description) {
        key = "advancements." + key;//TODO add modid eventually...
        //key = "advancements." + modid + "." + key;
        add(key + ".title", title);
        add(key + ".descr", description);//TODO rename eventually.
        //add(key + ".description", description);
    }

    protected void addAttribute(RegistryObject<Attribute> attribute, String value) {
        add(toLanguageKey(attribute.getId(), "attribute"), value);
    }

    protected void addBlockFromId(RegistryObject<? extends Block> block) {
        addBlock(block, Arrays.stream(block.getId().getPath().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1))
                .collect(Collectors.joining(" ")));
    }

    protected void addEntityFromId(RegistryObject<? extends EntityType<?>> entity) {
        addEntityType(entity, Arrays.stream(entity.getId().getPath().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1))
                .collect(Collectors.joining(" ")));
    }

    protected void addCommand(String key, String value) {
        add("command." + modid + "." + key, value);
    }

    protected void addCommandO(String key, String value) {//TODO move to command.modid.key instead
        add(modid + ".command." + key, value);
    }

    protected void addContainer(String key, String value) {
        add("container." + modid + "." + key, value);
    }

    protected void addDeathMessage(String damageSource, String generic, @Nullable String item, @Nullable String player) {
        String key = "death.attack." + damageSource;
        add(key, generic);
        if (item != null) add(key + ".item", item);
        if (player != null) add(key + ".player", player);
    }

    protected void addGamerule(GameRules.Key<?> rule, String value, @Nullable String description) {
        String key = "gamerule." + rule.getId();
        add(key, value);
        if (description != null) add(key + ".description", description);
    }

    protected void addKey(KeyMapping key, String value) {
        add(key.getName(), value);
    }

    protected void addItemFromId(RegistryObject<? extends Item> item) {
        addItem(item, Arrays.stream(item.getId().getPath().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1))
                .collect(Collectors.joining(" ")));
    }

    protected void addMessage(String key, String value) {
        add("message." + modid + "." + key, value);
    }

    protected void addMisc(String key, String value) {
        add("misc." + modid + "." + key, value);
    }

    protected void addScreen(String key, String value) {
        add("screen." + modid + "." + key, value);
    }

    protected void addSound(RegistryObject<SoundEvent> sound, String value) {
        add(toLanguageKey(sound.getId(), "subtitles"), value);
    }

    protected void addSoundO(RegistryObject<SoundEvent> sound, String value) {
        add("subtitles." + sound.getId().getPath(), value);
        ;
    }

    protected void addTooltip(String key, String value) {
        add("tooltip." + modid + "." + key, value);
    }

    protected void addText(String key, String value) {
        add("text." + modid + "." + key, value);
    }


    protected void addAbility(String key, String value) {
        add("ability." + modid + "." + key, value);
    }

    protected void addAbilityO(String key, String value) {//TODO move to ability.modid.key instead
        add(modid + ".ability." + key, value);
    }

    protected void addEntityDialogues(String key, String value) {
        add("entity_dialogues." + modid + "." + key, value);
    }

    protected void addEntityDialoguesO(String key, String value) {//TODO move to entity_dialogues.modid.key instead
        add(modid + ".entity_dialogues." + key, value);
    }

    protected void addGui(String key, String value) {
        add("gui." + modid + "." + key, value);
    }

    protected void addGuiO(String key, String value) {//TODO move to gui.modid.key instead
        add(modid + ".gui." + key, value);
    }

    protected void addJeiDescriptions(String key, String value) {
        add("jei_descriptions." + modid + "." + key, value);
    }

    protected void addJeiDescriptionsO(String key, String value) {//TODO move to jei_descriptions.modid.key instead
        add(modid + ".jei_descriptions." + key, value);
    }

    protected void addEffect(RegistryObject<? extends MobEffect> effect, String value, @Nullable String description) {
        String key = effect.get().getDescriptionId();
        add(key, value);
        if (description != null) add(key + ".description", description);
    }

    protected void addEnchantment(RegistryObject<? extends Enchantment> enchantment, String value, @Nullable String description, @Nullable String jeiDescription) {
        String key = enchantment.get().getDescriptionId();
        add(key, value);
        if (description != null) add(key + ".desc", description);
        if (jeiDescription != null) add(key + ".jei_desc", jeiDescription);
    }


    protected String toLanguageKey(ResourceLocation loc, String type) {
        return type + "." + loc.getNamespace() + "." + loc.getPath();
    }
}
