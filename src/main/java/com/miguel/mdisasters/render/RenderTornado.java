package com.miguel.mdisasters.render;

import com.miguel.mdisasters.objects.entities.EntityTornado;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import scala.xml.Null;

public class RenderTornado extends Render<EntityTornado> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mdisasters", "textures/particles/tornado.png");

    public RenderTornado(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityTornado entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        // Configuraciones de Renderizado OpenGL (Transparencia y Render por ambos lados)
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull(); // Permite ver las caras internas del embudo
        GlStateManager.disableLighting(); // Mantiene el tornado iluminado/brillante

        this.bindEntityTexture(entity);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        int layers = 15;        // Altura/Anillos del tornado
        double maxHeight = 12.0; // Altura total
        float rotation = (entity.ticksExisted + partialTicks) * 15.0F; // Velocidad de giro

        // Dibujar embudo en capas
        for (int i = 0; i < layers; i++) {
            double progress = (double) i / layers;
            double currentY = progress * maxHeight;

            // El radio aumenta hacia arriba (forma de cono)
            double radius = 0.8 + Math.pow(progress, 1.5) * 5.0;

            // Desfase en X/Z para simular la turbulencia/curvatura del viento
            double offsetX = Math.sin((entity.ticksExisted + i) * 0.2) * 0.3;
            double offsetZ = Math.cos((entity.ticksExisted + i) * 0.2) * 0.3;

            GlStateManager.pushMatrix();
            GlStateManager.translate(offsetX, currentY, offsetZ);
            GlStateManager.rotate(rotation + (i * 20F), 0.0F, 1.0F, 0.0F);

            // Dibujar un plano o cuadrilátero rotatorio por capa
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

            float alpha = 0.4F - ((float) progress * 0.15F); // Más opaco abajo, más transparente arriba

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