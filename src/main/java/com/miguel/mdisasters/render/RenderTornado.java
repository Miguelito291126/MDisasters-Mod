package com.miguel.mdisasters.render;

import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.objects.entities.EntityTornado;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderTornado extends Render<EntityTornado> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mdisasters", "textures/particles/tornado.png");

    public RenderTornado(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityTornado entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        // Configuraciones de Renderizado OpenGL
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull(); // Permite ver las caras internas
        GlStateManager.disableLighting(); // Mantiene la luminosidad

        this.bindEntityTexture(entity);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        double maxHeight = MDConfig.TORNADO.tornadoHeight;
        double maxRadius = MDConfig.TORNADO.tornadoWidth / 2.0;


        int layers = Math.max(10, (int) (maxHeight / 2.0));


        float rotationSpeed = (float) (15.0F * (MDConfig.TORNADO.speed / 0.2));
        float rotation = (entity.ticksExisted + partialTicks) * rotationSpeed;

        for (int i = 0; i < layers; i++) {
            double progress = (double) i / layers;
            double currentY = progress * maxHeight;

            // El radio escala de forma cónica desde la base hasta la anchura máxima configurada
            double radius = 0.8 + Math.pow(progress, 1.5) * (maxRadius - 0.8);

            // Desfase turbulento en X/Z
            double offsetX = Math.sin((entity.ticksExisted + i) * 0.2) * 0.3;
            double offsetZ = Math.cos((entity.ticksExisted + i) * 0.2) * 0.3;

            GlStateManager.pushMatrix();
            GlStateManager.translate(offsetX, currentY, offsetZ);
            GlStateManager.rotate(rotation + (i * 20F), 0.0F, 1.0F, 0.0F);

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

            float alpha = 0.4F - ((float) progress * 0.15F);

            buffer.pos(-radius, 0, -radius).tex(0, 0).color(0.8F, 0.8F, 0.8F, alpha).endVertex();
            buffer.pos(radius, 0, -radius).tex(1, 0).color(0.8F, 0.8F, 0.8F, alpha).endVertex();
            buffer.pos(radius, 0, radius).tex(1, 1).color(0.8F, 0.8F, 0.8F, alpha).endVertex();
            buffer.pos(-radius, 0, radius).tex(0, 1).color(0.8F, 0.8F, 0.8F, alpha).endVertex();

            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTornado entity) {
        return TEXTURE;
    }
}