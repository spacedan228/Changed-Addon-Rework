package net.foxyas.changedaddon.datagen.customData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AdvancementWriter {

   private static final Gson GSON = new GsonBuilder()
           .setPrettyPrinting()
           .disableHtmlEscaping()
           .create();

   public static void write(
           HashCache cache,
           DataGenerator generator,
           ResourceLocation id,
           Advancement.Builder builder) {
      try {
         JsonObject json = builder.serializeToJson();

         Path path = generator.getOutputFolder()
                 .resolve("data")
                 .resolve(id.getNamespace())
                 .resolve("advancements")
                 .resolve(id.getPath() + ".json");

//         Files.createDirectories(path.getParent());
//         Files.writeString(path, GSON.toJson(json));

         DataProvider.save(GSON, cache, json, path);

      } catch (IOException e) {
         throw new RuntimeException("Failed to write advancement " + id, e);
      }
   }
}
