package net.foxyas.changedaddon.util;

import net.foxyas.changedaddon.item.LaserPointerItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemUtils {

    /**
     * Dyes a piece of dyeable leather armor with the given dyes.
     * This replicates the vanilla crafting table dyeing logic.
     *
     * @param input The armor stack to dye.
     * @param dyes  The list of dyes to apply.
     * @return A new dyed ItemStack, or ItemStack.EMPTY if not dyeable.
     */
    public static ItemStack dyeArmor(ItemStack input, List<DyeItem> dyes) {
        Item item = input.getItem();
        if (!(item instanceof DyeableLeatherItem dyeable)) {
            return ItemStack.EMPTY; // Not dyeable
        }

        ItemStack result = input.copy();
        result.setCount(1);

        int[] colorComponents = new int[3]; // RGB accumulation
        int totalMaxComponent = 0;          // Sum of max channel per color
        int colorCount = 0;                 // How many colors were added

        // Include existing color if armor already dyed
        if (dyeable.hasCustomColor(input)) {
            int color = dyeable.getColor(input);
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;

            totalMaxComponent += (int) (Math.max(r, Math.max(g, b)) * 255.0F);
            colorComponents[0] += (int) (r * 255.0F);
            colorComponents[1] += (int) (g * 255.0F);
            colorComponents[2] += (int) (b * 255.0F);
            colorCount++;
        }

        // Add all dye colors
        for (DyeItem dye : dyes) {
            float[] rgb = dye.getDyeColor().getTextureDiffuseColors(); // [r,g,b] between 0-1
            int r = (int) (rgb[0] * 255.0F);
            int g = (int) (rgb[1] * 255.0F);
            int b = (int) (rgb[2] * 255.0F);

            totalMaxComponent += Math.max(r, Math.max(g, b));
            colorComponents[0] += r;
            colorComponents[1] += g;
            colorComponents[2] += b;
            colorCount++;
        }

        if (colorCount == 0) {
            return ItemStack.EMPTY; // No dyes, return empty
        }

        // Average color
        int r = colorComponents[0] / colorCount;
        int g = colorComponents[1] / colorCount;
        int b = colorComponents[2] / colorCount;

        float avgMax = (float) totalMaxComponent / (float) colorCount;
        float maxComponent = (float) Math.max(r, Math.max(g, b));

        // Normalize color so brightness is preserved
        r = (int) ((float) r * avgMax / maxComponent);
        g = (int) ((float) g * avgMax / maxComponent);
        b = (int) ((float) b * avgMax / maxComponent);

        int finalColor = (r << 16) | (g << 8) | b;

        dyeable.setColor(result, finalColor);
        return result;
    }

    /**
     * Dyes a piece of dyeable leather armor with the given dyes.
     * This replicates the vanilla crafting table dyeing logic.
     *
     * @param input The armor stack to dye.
     * @param dyes  The list of dyes to apply.
     * @return A new dyed ItemStack, or ItemStack.EMPTY if not dyeable.
     */
    public static ItemStack dyeLaser(ItemStack input, List<DyeItem> dyes) {
        Item item = input.getItem();
        if (!(item instanceof LaserPointerItem dyeable)) {
            return ItemStack.EMPTY; // Not dyeable
        }

        ItemStack result = input.copy();
        result.setCount(1);

        int[] colorComponents = new int[3]; // RGB accumulation
        int totalMaxComponent = 0;          // Sum of max channel per color
        int colorCount = 0;                 // How many colors were added

        // Include existing color if armor already dyed
        {
            int color = LaserPointerItem.getColor(input);
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;

            totalMaxComponent += (int) (Math.max(r, Math.max(g, b)) * 255.0F);
            colorComponents[0] += (int) (r * 255.0F);
            colorComponents[1] += (int) (g * 255.0F);
            colorComponents[2] += (int) (b * 255.0F);
            colorCount++;
        }

        // Add all dye colors
        for (DyeItem dye : dyes) {
            float[] rgb = dye.getDyeColor().getTextureDiffuseColors(); // [r,g,b] between 0-1
            int r = (int) (rgb[0] * 255.0F);
            int g = (int) (rgb[1] * 255.0F);
            int b = (int) (rgb[2] * 255.0F);

            totalMaxComponent += Math.max(r, Math.max(g, b));
            colorComponents[0] += r;
            colorComponents[1] += g;
            colorComponents[2] += b;
            colorCount++;
        }

        if (colorCount == 0) {
            return ItemStack.EMPTY; // No dyes, return empty
        }

        // Average color
        int r = colorComponents[0] / colorCount;
        int g = colorComponents[1] / colorCount;
        int b = colorComponents[2] / colorCount;

        float avgMax = (float) totalMaxComponent / (float) colorCount;
        float maxComponent = (float) Math.max(r, Math.max(g, b));

        // Normalize color so brightness is preserved
        r = (int) ((float) r * avgMax / maxComponent);
        g = (int) ((float) g * avgMax / maxComponent);
        b = (int) ((float) b * avgMax / maxComponent);

        int finalColor = (r << 16) | (g << 8) | b;

        LaserPointerItem.setLaserColor(result, finalColor);
        return result;
    }
}
