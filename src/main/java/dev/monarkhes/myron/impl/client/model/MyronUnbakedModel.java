package dev.monarkhes.myron.impl.client.model;

import com.mojang.datafixers.util.Pair;
import de.javagl.obj.Obj;
import dev.monarkhes.myron.impl.client.Myron;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class MyronUnbakedModel implements UnbakedModel {
    private final Obj obj;
    private final Map<String, MyronMaterial> materials;
    private final Collection<Material> textureDependencies;
    private final Material sprite;
    private final ItemTransforms transform;
    private final boolean isSideLit;
    private final boolean isBlock;

    public MyronUnbakedModel(@Nullable Obj obj, @Nullable Map<String, MyronMaterial> materials, Collection<Material> textureDependencies, Material sprite, ItemTransforms modelTransformation, boolean isSideLit, boolean isBlock) {
        this.obj = obj;
        this.materials = materials;
        this.textureDependencies = textureDependencies;
        this.sprite = sprite;
        this.transform = modelTransformation;
        this.isSideLit = isSideLit;
        this.isBlock = isBlock;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return this.textureDependencies;
    }

    @Override
    public @Nullable BakedModel bake(ModelBakery loader, Function<Material, TextureAtlasSprite> textureGetter, ModelState bakeSettings, ResourceLocation modelId) {
        Mesh mesh;

        if (obj == null)
            // Try to load the obj (previous behavior)
            mesh = Myron.load(modelId, textureGetter, bakeSettings, isBlock);
        else
            // We already loaded the obj earlier in AbstractObjLoader. Don't use the external utility to re-load the obj
            // (it works only on absolute identifiers, not ModelIdentifiers like 'myron:torus#inventory')
            mesh = Myron.build(obj, materials, textureGetter, bakeSettings, isBlock);

        Myron.MESHES.put(modelId, mesh);

        return new MyronBakedModel(mesh, this.transform, textureGetter.apply(this.sprite), this.isSideLit);
    }
}
