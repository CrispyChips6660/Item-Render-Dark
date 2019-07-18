/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.rendering;

import java.io.File;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import itemrender.ItemRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Created by Jerrell Fang on 2/23/2015.
 *
 * @author Meow J
 */
public class Renderer
{

    public static void renderEntity(LivingEntity entity, FBOHelper fbo, String filenameSuffix, boolean renderPlayer)
    {
        Minecraft minecraft = Minecraft.getInstance();
        float scale = ItemRender.renderScale;
        fbo.begin();

        AxisAlignedBB aabb = entity.getRenderBoundingBox();
        double minX = aabb.minX - entity.posX;
        double maxX = aabb.maxX - entity.posX;
        double minY = aabb.minY - entity.posY;
        double maxY = aabb.maxY - entity.posY;
        double minZ = aabb.minZ - entity.posZ;
        double maxZ = aabb.maxZ - entity.posZ;

        double minBound = Math.min(minX, Math.min(minY, minZ));
        double maxBound = Math.max(maxX, Math.max(maxY, maxZ));

        double boundLimit = Math.max(Math.abs(minBound), Math.abs(maxBound));

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(-boundLimit * 0.75, boundLimit * 0.75, -boundLimit * 1.25, boundLimit * 0.25, -100.0, 100.0);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        // Render entity
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0, 0, 50.0F);

        if (renderPlayer)
            GlStateManager.scalef(-1F, 1F, 1F);
        else
            GlStateManager.scalef(-scale, scale, scale);

        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;
        GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);

        GlStateManager.rotatef((float) Math.toDegrees(Math.asin(Math.tan(Math.toRadians(30)))), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(-45, 0.0F, 1.0F, 0.0F);

        entity.renderYawOffset = (float) Math.atan(1 / 40.0F) * 20.0F;
        entity.rotationYaw = (float) Math.atan(1 / 40.0F) * 40.0F;
        entity.rotationPitch = -((float) Math.atan(1 / 40.0F)) * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        GlStateManager.translatef(0.0F, 0.0F, 0.0F);
        EntityRendererManager rendermanager = minecraft.getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
        rendermanager.setRenderShadow(true);
        entity.renderYawOffset = f2;
        entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = f5;
        entity.rotationYawHead = f6;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();

        fbo.end();
        String name = entity.getType().getRegistryName().getPath();
        fbo.saveToFile(new File(minecraft.gameDir, renderPlayer ? "rendered/player.png" : String.format("rendered/entity_%s%s.png", name.replaceAll("[^A-Za-z0-9()\\[\\]]", ""), filenameSuffix)));
        fbo.restoreTexture();
    }

    public static void renderItem(ItemStack itemStack, FBOHelper fbo, String filenameSuffix, ItemRenderer itemRenderer)
    {
        Minecraft minecraft = Minecraft.getInstance();
        float scale = ItemRender.renderScale;
        fbo.begin();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, 16, 0, 16, -150.0F, 150.0F);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();

        GlStateManager.translatef(8 * (1 - scale), 8 * (1 - scale), 0);
        GlStateManager.scalef(scale, scale, scale);

        itemRenderer.renderItemIntoGUI(itemStack, 0, 0);

        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();

        fbo.end();
        fbo.saveToFile(new File(minecraft.gameDir, String.format("rendered/item_%s_%s.png", itemStack.getTranslationKey().replaceAll("[^A-Za-z0-9()\\[\\]]", ""), filenameSuffix)));
        fbo.restoreTexture();
    }

    public static String getItemBase64(ItemStack itemStack, FBOHelper fbo, ItemRenderer itemRenderer)
    {
        String base64;
        float scale = ItemRender.renderScale;
        fbo.begin();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, 16, 0, 16, -150.0F, 150.0F);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();

        GlStateManager.translatef(8 * (1 - scale), 8 * (1 - scale), 0);
        GlStateManager.scalef(scale, scale, scale);

        itemRenderer.renderItemIntoGUI(itemStack, 0, 0);

        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();

        fbo.end();
        base64 = fbo.getBase64();
        fbo.restoreTexture();
        return base64;
    }

    public static String getEntityBase64(EntityType Entitymob, FBOHelper fbo)
    {
        String base64;
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = Entitymob.create(minecraft.world);
        if (!(entity instanceof LivingEntity))
        {
            return null;
        }
        else
        {
            LivingEntity living = (LivingEntity) entity;
            float scale = ItemRender.renderScale;
            fbo.begin();
            AxisAlignedBB aabb = living.getRenderBoundingBox();
            double minX = aabb.minX - entity.posX;
            double maxX = aabb.maxX - entity.posX;
            double minY = aabb.minY - entity.posY;
            double maxY = aabb.maxY - entity.posY;
            double minZ = aabb.minZ - entity.posZ;
            double maxZ = aabb.maxZ - entity.posZ;

            double minBound = Math.min(minX, Math.min(minY, minZ));
            double maxBound = Math.max(maxX, Math.max(maxY, maxZ));

            double boundLimit = Math.max(Math.abs(minBound), Math.abs(maxBound));

            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(-boundLimit * 0.75, boundLimit * 0.75, -boundLimit * 1.25, boundLimit * 0.25, -100.0, 100.0);

            GlStateManager.matrixMode(GL11.GL_MODELVIEW);

            // Render entity
            GlStateManager.enableColorMaterial();
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0, 0, 50.0F);
            GlStateManager.scalef(-scale, scale, scale);

            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float f2 = living.renderYawOffset;
            float f3 = living.rotationYaw;
            float f4 = living.rotationPitch;
            float f5 = living.prevRotationYawHead;
            float f6 = living.rotationYawHead;
            GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);

            GlStateManager.rotatef((float) Math.toDegrees(Math.asin(Math.tan(Math.toRadians(30)))), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(-45, 0.0F, 1.0F, 0.0F);

            living.renderYawOffset = (float) Math.atan(1 / 40.0F) * 20.0F;
            living.rotationYaw = (float) Math.atan(1 / 40.0F) * 40.0F;
            living.rotationPitch = -((float) Math.atan(1 / 40.0F)) * 20.0F;
            living.rotationYawHead = entity.rotationYaw;
            living.prevRotationYawHead = entity.rotationYaw;
            GlStateManager.translatef(0.0F, 0.0F, 0.0F);
            EntityRendererManager rendermanager = minecraft.getRenderManager();
            rendermanager.setPlayerViewY(180.0F);
            rendermanager.setRenderShadow(false);
            rendermanager.renderEntity(living, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
            rendermanager.setRenderShadow(true);
            living.renderYawOffset = f2;
            living.rotationYaw = f3;
            living.rotationPitch = f4;
            living.prevRotationYawHead = f5;
            living.rotationYawHead = f6;
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.activeTexture(GLX.GL_TEXTURE1);
            GlStateManager.disableTexture();
            GlStateManager.activeTexture(GLX.GL_TEXTURE0);

            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.popMatrix();

            fbo.end();
            base64 = fbo.getBase64();
            fbo.restoreTexture();
            return base64;
        }
    }
}
