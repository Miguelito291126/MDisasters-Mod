package com.miguel.mdisasters.render;

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
    // 1. CORRECCIÓN DE LA RUTA: Solo pones la ruta relativa dentro de textures/ sin la extensión .png
    // Añade "textures/" y la extensión ".png"
    private static final ResourceLocation TEXTURE = new ResourceLocation("mdisasters", "textures/blocks/volcano_block.png");

    public RenderMeteor(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityMeteor entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        // 2. Vincula la textura correctamente
        bindTexture(TEXTURE);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        // Solo UN buf.begin() para todo el cubo
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