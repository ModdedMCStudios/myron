package dev.monarkhes.myron.impl.client.model;

import dev.monarkhes.myron.impl.client.Myron;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class MyronBakedModel implements BakedModel, FabricBakedModel {
    private final Mesh mesh;
    private final ItemTransforms transformation;
    private final TextureAtlasSprite sprite;
    private final boolean isSideLit;

    private List<BakedQuad> backupQuads = null;

    public MyronBakedModel(Mesh mesh, ItemTransforms transformation, TextureAtlasSprite sprite, boolean isSideLit) {
        this.mesh = mesh;
        this.transformation = transformation;
        this.sprite = sprite;
        this.isSideLit = isSideLit;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<net.minecraft.util.RandomSource> randomSupplier, RenderContext context) {
        if (this.mesh != null) {
            context.meshConsumer().accept(mesh);
        } else {
            Myron.LOGGER.warn("Mesh is null while emitting block quads for block {}", state.getBlock().getName().getContents());
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<net.minecraft.util.RandomSource> randomSupplier, RenderContext context) {
        if (this.mesh != null) {
            context.meshConsumer().accept(mesh);
        } else {
            Myron.LOGGER.warn("Mesh is null while emitting block quads for item {}", stack.getItem().getDescription().getContents());
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, net.minecraft.util.RandomSource random) {
        if (this.backupQuads == null) {
            this.backupQuads = new ArrayList<>();

            mesh.forEach(quadView -> {
                this.backupQuads.add(quadView.toBakedQuad(0, this.sprite, false));
            });
        }

        return this.backupQuads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return this.isSideLit;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.sprite;
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.transformation;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }
}
