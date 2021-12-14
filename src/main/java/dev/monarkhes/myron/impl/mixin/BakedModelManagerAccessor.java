package dev.monarkhes.myron.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

@Mixin(ModelManager.class)
public interface BakedModelManagerAccessor {
    @Accessor
    Map<ResourceLocation, BakedModel> getBakedRegistry();
}
