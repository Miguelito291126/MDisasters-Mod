package com.miguel.mdisasters.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderEmpty<T extends Entity> extends Render<T> {

    public RenderEmpty(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F; // Desactiva la sombra en el suelo
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Al NO llamar a super.doRender(), evitas que Minecraft dibuje el fuego, las etiquetas de texto o las sombras
    }

    @Override
    protected boolean canRenderName(T entity) {
        return false; // Desactiva el renderizado de nombres si la entidad tuviera uno
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}