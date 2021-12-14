package dev.monarkhes.myron.api;

import dev.monarkhes.myron.impl.mixin.BakedModelManagerAccessor;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class Myron {
    /**
     * Gets a baked model by its ID.
     * Useful for models that aren't associated with blocks/items, and therefore don't have a {@link ModelResourceLocation}.
     * @param id the id of the model to fetch
     * @return the model itself
     */
    public static @Nullable BakedModel getModel(ResourceLocation id) {
        return ((BakedModelManagerAccessor) Minecraft.getInstance().getModelManager()).getBakedRegistry().get(id);
    }

    public static @Nullable Mesh load(ResourceLocation modelPath, Function<Material, TextureAtlasSprite> textureGetter, ModelState bakeSettings, boolean isBlock) {
        return dev.monarkhes.myron.impl.client.Myron.load(modelPath, textureGetter, bakeSettings, isBlock);
    }
}
