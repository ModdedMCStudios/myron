package dev.monarkhes.myron.impl.client.obj;

import com.google.gson.*;
import dev.monarkhes.myron.impl.client.Myron;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ObjLoader extends AbstractObjLoader implements ModelResourceProvider, ModelVariantProvider {
    private static final Gson GSON = (new GsonBuilder())
            .registerTypeAdapter(ItemTransforms.class, new ModelTransformDeserializer())
            .registerTypeAdapter(ItemTransform.class, new TransformDeserializer())
            .create();

    private final ResourceManager resourceManager;

    public ObjLoader(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }


    @Override
    public @Nullable UnbakedModel loadModelResource(ResourceLocation identifier, ModelProviderContext modelProviderContext) {
        return loadModel(this.resourceManager, identifier, ItemTransforms.NO_TRANSFORMS, true);
    }

    @Override
    public @Nullable UnbakedModel loadModelVariant(ModelResourceLocation modelIdentifier, ModelProviderContext modelProviderContext) {
        ResourceLocation resource = new ResourceLocation(
                modelIdentifier.getNamespace(),
                "models/item/" + modelIdentifier.getPath () + ".json");

        var res = this.resourceManager.getResource(resource);
        if (!modelIdentifier.getVariant().equals("inventory") || res.isEmpty()) {
            return null;
        }

        try (Reader reader = new InputStreamReader(res.get().open())) {
            JsonObject rawModel = GsonHelper.parse(reader);

            JsonElement model = rawModel.get("model");
            if (!(model instanceof JsonPrimitive) || !((JsonPrimitive) model).isString()) {
                return null;
            }

            ResourceLocation modelPath = new ResourceLocation(model.getAsString());
            ItemTransforms transformation = this.getTransformation(rawModel);

            boolean isSideLit = true;

            if (rawModel.has("gui_light")) {
                isSideLit = GsonHelper.getAsString(rawModel, "gui_light").equals("side");
            }

            return this.loadModel(this.resourceManager, modelPath, transformation, isSideLit);
        } catch (IOException e) {
            Myron.LOGGER.warn("Failed to load model {}:\n{}", resource, e.getMessage());
            return null;
        }
    }

    private ItemTransforms getTransformation(JsonObject rawModel) throws IOException {
        if (rawModel.has("display")) {
            JsonObject rawTransform = GsonHelper.getAsJsonObject(rawModel, "display");
            return GSON.fromJson(rawTransform, ItemTransforms.class);
        } else if (rawModel.has("parent")) {
            ResourceLocation parent = new ResourceLocation(GsonHelper.getAsString(rawModel, "parent"));
            parent = new ResourceLocation(parent.getNamespace(), "models/" + parent.getPath() + ".json");
            return this.getTransformation(parent);
        } else {
            return ItemTransforms.NO_TRANSFORMS;
        }
    }

    private ItemTransforms getTransformation(ResourceLocation model) throws IOException {
        var res = this.resourceManager.getResource(model);
        if (res.isPresent()) {
            Reader reader = new InputStreamReader(res.get().open());
            return getTransformation(GsonHelper.parse(reader));
        } else {
            return ItemTransforms.NO_TRANSFORMS;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ModelTransformDeserializer extends ItemTransforms.Deserializer {
        public ModelTransformDeserializer() {
            super();
        }
    }
    @Environment(EnvType.CLIENT)
    public static class TransformDeserializer extends ItemTransform.Deserializer {
        public TransformDeserializer() {
            super();
        }
    }
}
