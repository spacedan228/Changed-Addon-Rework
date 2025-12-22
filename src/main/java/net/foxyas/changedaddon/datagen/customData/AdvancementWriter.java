package net.foxyas.changedaddon.datagen.customData;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class AdvancementWriter {

   public static CompletableFuture<?> write(
           CachedOutput cache,
           PackOutput output,
           ResourceLocation id,
           Advancement.Builder builder) {
      try {
         JsonObject json = builder.serializeToJson();

         Path path = output.getOutputFolder()
                 .resolve("data")
                 .resolve(id.getNamespace())
                 .resolve("advancements")
                 .resolve(id.getPath() + ".json");

          return DataProvider.saveStable(cache, json, path);
      } catch (Exception e) {
         throw new RuntimeException("Failed to write advancement " + id, e);
      }
   }
}
