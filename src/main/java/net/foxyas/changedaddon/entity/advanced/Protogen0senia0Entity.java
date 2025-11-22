package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.entity.api.IDynamicPawColor;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PlayMessages;

import java.awt.*;

public class Protogen0senia0Entity extends AbstractProtogenEntity implements IDynamicPawColor {

    public Protogen0senia0Entity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    public Protogen0senia0Entity(PlayMessages.SpawnEntity ignoredSpawnEntity, Level level) {
        this(ChangedAddonEntities.PROTOGEN_0SENIA0.get(), level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = ChangedEntity.createLatexAttributes();
        builder.add(ChangedAttributes.TRANSFUR_DAMAGE.get(), 3f);
        builder.add(Attributes.MOVEMENT_SPEED, 1.1);
        builder.add(Attributes.MAX_HEALTH, 24);
        builder.add(Attributes.ARMOR, 4);
        builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder.add(Attributes.FOLLOW_RANGE, 40);
        builder.add(ForgeMod.SWIM_SPEED.get(), 0.95f);
        return builder;
    }



    public boolean isOrganic() {
        return true;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.parseHex("#4d0ddb");
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
    }

    @Override
    public Color getPawBeansColor() {
        return new Color(39, 53, 95);
    }
}
