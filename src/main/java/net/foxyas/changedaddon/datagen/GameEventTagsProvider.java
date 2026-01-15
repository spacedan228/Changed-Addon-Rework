package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.GameEventTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class GameEventTagsProvider extends net.minecraft.data.tags.GameEventTagsProvider {
   public GameEventTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
      super(pGenerator, ChangedAddonMod.MODID, existingFileHelper);
   }

   @Override
   protected void addTags() {
      tag(ChangedAddonTags.GameEvents.CAN_WAKE_UP_ALPHAS).addTag(GameEventTags.VIBRATIONS);
   }

   public String getName() {
      return "Game Event Tags";
   }
}