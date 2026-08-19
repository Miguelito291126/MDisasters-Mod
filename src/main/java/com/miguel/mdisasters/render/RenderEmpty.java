package com.miguel.mdisasters.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderEmpty<T extends Entity> extends Render<T> {

    public RenderEmpty(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // No renderiza ningún modelo ni textura
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}