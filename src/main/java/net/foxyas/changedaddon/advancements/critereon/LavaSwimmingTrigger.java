package net.foxyas.changedaddon.advancements.critereon;

import com.google.gson.JsonObject;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class LavaSwimmingTrigger extends SimpleCriterionTrigger<LavaSwimmingTrigger.Instance> {
    private static final ResourceLocation ID = ChangedAddonMod.resourceLoc("lava_swimming");

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    @Override
    protected @NotNull Instance createInstance(@NotNull JsonObject json, @NotNull ContextAwarePredicate playerPredicate, @NotNull DeserializationContext context) {
        return new Instance(playerPredicate);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(@NotNull ContextAwarePredicate playerPredicate) {
            super(ID, playerPredicate);
        }

        @Override
        public @NotNull JsonObject serializeToJson(@NotNull SerializationContext context) {
            return super.serializeToJson(context);
        }
    }
}
