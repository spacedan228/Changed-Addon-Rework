package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AlphaSerumSyringeItem extends AbstractSyringeItem {

    public AlphaSerumSyringeItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.CHANGED_ADDON_MAIN_TAB)
                .stacksTo(64)
                .rarity(Rarity.RARE)
        );
    }

    @Override
    public void applyEffectsAfterUse(@NotNull ItemStack pStack, Level level, LivingEntity entity) {
        super.applyEffectsAfterUse(pStack, level, entity);

        if (!(entity instanceof Player player)) return;

        ChangedAddonVariables.PlayerVariables playerVars = ChangedAddonVariables.ofOrDefault(player);

        if (!ProcessTransfur.isPlayerTransfurred(player)) {
            if (playerVars.showWarns && !player.level.isClientSide())
                player.displayClientMessage(new TranslatableComponent("changed_addon.untransfur.no_effect"), true);
            return;
        }

        TransfurVariantInstance<?> transfurVariant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (transfurVariant == null) {
            return;
        }
        
        ChangedEntity changedEntity = transfurVariant.getChangedEntity();
        if (!(changedEntity instanceof IAlphaAbleEntity iAlphaAbleEntity)) {
            return;
        }
        
        if (iAlphaAbleEntity.isAlpha()) {
            if (playerVars.showWarns && !player.level.isClientSide())
                player.displayClientMessage(new TranslatableComponent("changed_addon.untransfur.no_effect"), true);
            return; 
        }

        iAlphaAbleEntity.setAlpha(true);
        iAlphaAbleEntity.setAlphaScale(0.75f);
    }
}
