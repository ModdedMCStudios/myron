package dev.monarkhes.myron.impl.mixin;

import dev.monarkhes.myron.impl.client.obj.ObjLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = ModelBakery.class, priority = 100)
public abstract class MixinModelLoader {
    @Shadow @Final private ResourceManager resourceManager;

    @Shadow protected abstract void cacheAndQueueDependencies(ResourceLocation resourceLocation, UnbakedModel unbakedModel);

    @Unique private ModelResourceProvider objModelProvider;

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void addObjModel(ResourceLocation id, CallbackInfo ci) {
        if (this.objModelProvider == null) {
            this.objModelProvider = new ObjLoader(this.resourceManager);
        }

        try {
            @Nullable UnbakedModel model = this.objModelProvider.loadModelResource(id, null);

            if (model != null) {
                this.cacheAndQueueDependencies(id, model);
                ci.cancel();
            }
        } catch (ModelProviderException e) {
            e.printStackTrace();
        }
    }
}
