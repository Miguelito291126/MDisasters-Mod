package com.miguel.mdisasters.render;

import com.miguel.mdisasters.objects.entities.EntityTsunami;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderTsunami extends Render<EntityTsunami> {

    // Dimensiones de la pared de bloques de agua
    private static final int WAVE_WIDTH = EntityTsunami.waveWidth;  // Ancho en bloques
    private static final int WAVE_HEIGHT =  EntityTsunami.waveHeight;  // Altura en bloques
    private static final int WAVE_DEPTH = 3;   // Grosor en bloques

    public RenderTsunami(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityTsunami entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        // Posicionar el render en la entidad
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);

        // Enlazar la textura general de bloques de Minecraft (atlas de terreno)
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        // Desactivar iluminación para que la masa de agua no se vea oscura por dentro
        GlStateManager.disableLighting();

        BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

        int halfWidth = WAVE_WIDTH / 2;

        // Bucle para construir la pared de bloques de agua vanilla
        for (int w = -halfWidth; w <= halfWidth; w++) {
            for (int h = 0; h < WAVE_HEIGHT; h++) {
                for (int d = 0; d < WAVE_DEPTH; d++) {

                    // Curvatura de la ola: desplaza los bloques superiores hacia adelante
                    double curveOffset = Math.sin((double) h / WAVE_HEIGHT * Math.PI) * 1.5;

                    GlStateManager.pushMatrix();

                    // Centrar y posicionar cada cubo de agua
                    GlStateManager.translate(w, h, d + curveOffset);

                    // Renderizar el bloque de agua oficial de Minecraft
                    blockRenderer.renderBlockBrightness(Blocks.WATER.getDefaultState(), 1.0F);

                    GlStateManager.popMatrix();
                }
            }
        }

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTsunami entity) {
        // Al usar TextureMap.LOCATION_BLOCKS_TEXTURE arriba, este retorno no se utiliza directamente
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}