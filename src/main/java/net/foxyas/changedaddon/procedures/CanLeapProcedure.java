package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CanLeapProcedure {
	public static boolean execute(Entity entity) {
		Player player = (Player) entity;
		TransfurVariantInstance<?> LatexInstace = ProcessTransfur.getPlayerTransfurVariant(player);
		TransfurVariant<?> Variant = TransfurVariant.getEntityVariant(LatexInstace.getChangedEntity());
		if (Variant.is(ChangedAddonTransfurVariants.TransfurVariantTags.CAT_LIKE) || Variant.is(ChangedAddonTransfurVariants.TransfurVariantTags.LEOPARD_LIKE)){
			if (Variant.is(ChangedAddonTransfurVariants.LATEX_SNEP.get())
					|| Variant.is(ChangedAddonTransfurVariants.LUMINARCTIC_LEOPARD.get())
					|| Variant.is(ChangedAddonTransfurVariants.EXPERIMENT_10.get())
					|| Variant.is(ChangedAddonTransfurVariants.EXPERIMENT_10_BOSS.get())
					|| Variant.is(ChangedAddonTransfurVariants.LYNX.get())){
				return false;
			}
			return true;
		}
		return false;
	}

	public static boolean flyentity(Entity entity) {
		/*Player player = (Player) entity;
		TransfurVariantInstance LatexInstace = ProcessTransfur.getPlayerTransfurVariant(player);
		TransfurVariant Variant = TransfurVariant.getEntityVariant(LatexInstace.getChangedEntity());
		if (Variant.canGlide){
			return true;
		}*/
		return false;
	}
}
