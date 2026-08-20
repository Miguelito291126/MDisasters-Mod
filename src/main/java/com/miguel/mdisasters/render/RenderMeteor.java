package com.miguel.mdisasters.render;

import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.objects.entities.EntityMeteor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderMeteor extends Render<EntityMeteor> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mdisasters", "textures/blocks/volcano_block.png");

    public RenderMeteor(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityMeteor entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        // 1. Escalar el renderizador según el ancho y alto del MDConfig
        float scaleX = MDConfig.METEOR.meteorWidth;
        float scaleY = MDConfig.METEOR.meteorHeight;
        float scaleZ = MDConfig.METEOR.meteorWidth;
        GlStateManager.scale(scaleX, scaleY, scaleZ);

        // 2. Rotación continua en vuelo para dar efecto de caída realista
        float rotation = (entity.ticksExisted + partialTicks) * 10.0F;
        GlStateManager.rotate(rotation, 1.0F, 1.0F, 0.5F);

        bindTexture(TEXTURE);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        buf.begin(7, DefaultVertexFormats.POSITION_TEX);

        // Cara Frontal (Z+)
        buf.pos(-0.5, -0.5,  0.5).tex(0, 1).endVertex();
        buf.pos( 0.5, -0.5,  0.5).tex(1, 1).endVertex();
        buf.pos( 0.5,  0.5,  0.5).tex(1, 0).endVertex();
        buf.pos(-0.5,  0.5,  0.5).tex(0, 0).endVertex();

        // Cara Trasera (Z-)
        buf.pos( 0.5, -0.5, -0.5).tex(0, 1).endVertex();
        buf.pos(-0.5, -0.5, -0.5).tex(1, 1).endVertex();
        buf.pos(-0.5,  0.5, -0.5).tex(1, 0).endVertex();
        buf.pos( 0.5,  0.5, -0.5).tex(0, 0).endVertex();

        // Cara Superior (Y+)
        buf.pos(-0.5,  0.5, -0.5).tex(0, 1).endVertex();
        buf.pos(-0.5,  0.5,  0.5).tex(1, 1).endVertex();
        buf.pos( 0.5,  0.5,  0.5).tex(1, 0).endVertex();
        buf.pos( 0.5,  0.5, -0.5).tex(0, 0).endVertex();

        // Cara Inferior (Y-)
        buf.pos(-0.5, -0.5,  0.5).tex(0, 1).endVertex();
        buf.pos(-0.5, -0.5, -0.5).tex(1, 1).endVertex();
        buf.pos( 0.5, -0.5, -0.5).tex(1, 0).endVertex();
        buf.pos( 0.5, -0.5,  0.5).tex(0, 0).endVertex();

        // Cara Derecha (X+)
        buf.pos( 0.5, -0.5,  0.5).tex(0, 1).endVertex();
        buf.pos( 0.5, -0.5, -0.5).tex(1, 1).endVertex();
        buf.pos( 0.5,  0.5, -0.5).tex(1, 0).endVertex();
        buf.pos( 0.5,  0.5,  0.5).tex(0, 0).endVertex();

        // Cara Izquierda (X-)
        buf.pos(-0.5, -0.5, -0.5).tex(0, 1).endVertex();
        buf.pos(-0.5, -0.5,  0.5).tex(1, 1).endVertex();
        buf.pos(-0.5,  0.5,  0.5).tex(1, 0).endVertex();
        buf.pos(-0.5,  0.5, -0.5).tex(0, 0).endVertex();

        tess.draw();

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityMeteor entity) {
        return TEXTURE;
    }
}