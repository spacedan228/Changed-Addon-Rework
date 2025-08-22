package net.foxyas.changedaddon.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.ltxprogrammer.changed.advancements.critereon.TransfurPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GrabEntityTrigger extends SimpleCriterionTrigger<GrabEntityTrigger.Instance> {
    private static final ResourceLocation ID = ChangedAddonMod.resourceLoc("grab_entity");

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    @Override
    protected @NotNull Instance createInstance(@NotNull JsonObject json, EntityPredicate.@NotNull Composite playerPredicate, @NotNull DeserializationContext context) {
        // pega o nome se existir
        JsonElement elem = json.get("name");
        String name = (elem != null && elem.isJsonPrimitive() && elem.getAsJsonPrimitive().isString())
                ? elem.getAsString()
                : null;

        // pega o transfurPredicate se existir, senão ANY
        TransfurPredicate transfurPredicate = json.has("transfur")
                ? TransfurPredicate.fromJson(json.get("transfur"))
                : TransfurPredicate.ANY;

        return new Instance(playerPredicate, transfurPredicate, name);
    }

    public void trigger(ServerPlayer player, String name) {
        this.trigger(player, instance -> instance.matches(name));
    }

    public void trigger(ServerPlayer player, TransfurVariantInstance<?> transfurVariant, @Nullable String name) {
        this.trigger(player, instance -> instance.matches(transfurVariant, name));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        @Nullable
        private final String name;
        private final TransfurPredicate transfurPredicate;

        public Instance(EntityPredicate.Composite playerPredicate, TransfurPredicate transfurPredicate, @Nullable String name) {
            super(ID, playerPredicate);
            this.transfurPredicate = transfurPredicate != null ? transfurPredicate : TransfurPredicate.ANY;
            this.name = name;
        }

        public boolean matches(TransfurVariantInstance<?> transfurVariantInstance, @Nullable String name) {
            // se não especificou nome no json, só checa o transfur
            if (this.name == null)
                return this.transfurPredicate.matches(transfurVariantInstance);

            // se nome foi definido, precisa bater
            return name != null && name.equals(this.name);
        }

        public boolean matches(@Nullable String name) {
            if (this.name == null)
                return true; // se não especificou nome no json, qualquer nome serve
            return name != null && name.equals(this.name);
        }

        @Override
        public @NotNull JsonObject serializeToJson(@NotNull SerializationContext context) {
            JsonObject jsonObject = super.serializeToJson(context);
            if (name != null && !name.isEmpty())
                jsonObject.addProperty("name", name);
            if (transfurPredicate != null && transfurPredicate != TransfurPredicate.ANY)
                jsonObject.add("transfur", transfurPredicate.serializeToJson());

            return jsonObject;
        }
    }
}
