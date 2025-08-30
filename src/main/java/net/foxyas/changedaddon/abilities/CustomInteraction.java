package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.entity.advanced.AvaliEntity;
import net.foxyas.changedaddon.entity.advanced.LatexSnepEntity;
import net.foxyas.changedaddon.entity.defaults.AbstractLuminarcticLeopard;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomInteraction extends AbstractAbility<CustomInteractionInstance> {

    public CustomInteraction() {
        super(CustomInteractionInstance::new);
    }

    @Override
    public TranslatableComponent getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("changed_addon.ability.custom_interaction");
    }

    public ResourceLocation getTexture(IAbstractChangedEntity entity) {
        return new ResourceLocation("changed_addon:textures/screens/normal_paw.png");
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        if (entity.getChangedEntity() instanceof AvaliEntity) {
            return 160;
        }

        return super.getCoolDown(entity);
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        ArrayList<Component> description = new ArrayList<>(super.getAbilityDescription(entity));
        if (entity.getChangedEntity() instanceof LatexSnepEntity
                || entity.getChangedEntity() instanceof AvaliEntity
                || entity.getChangedEntity() instanceof AbstractLuminarcticLeopard) {
            if (entity.getChangedEntity() instanceof LatexSnepEntity) {
                description.add(new TranslatableComponent("changed_addon.ability.custom_interaction.have_interaction.latex_snep"));
            } else if (entity.getChangedEntity() instanceof AvaliEntity) {
                description.add(new TranslatableComponent("changed_addon.ability.custom_interaction.have_interaction.avali"));
            } else {
                description.add(new TranslatableComponent("changed_addon.ability.custom_interaction.have_interaction.luminarctic_leopards"));
            }
        }
        return description;
    }

    @Nullable
    @Override
    public Component getSelectedDisplayText(IAbstractChangedEntity entity) {
        if (entity.getChangedEntity() instanceof LatexSnepEntity || entity.getChangedEntity() instanceof AbstractLuminarcticLeopard) {
            return new TranslatableComponent("changed_addon.ability.custom_interaction.have_interaction");
        }
        if (entity.getChangedEntity() instanceof AvaliEntity) {
            return new TranslatableComponent("changed_addon.ability.custom_interaction.have_interaction.avali");
        }
        return super.getSelectedDisplayText(entity);
    }
}
