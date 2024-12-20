package moddedmite.xylose.bettergamesetting.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.xylose.bettergamesetting.api.IGameSetting;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = EntityRenderer.class, priority = 9999)
public abstract class EntityRenderMixin {
    @Shadow private float farPlaneDistance;
    @Shadow private Minecraft mc;
    @Shadow protected abstract void setupFog(int par1, float par2);
    @Shadow private float fogColorRed;
    @Shadow private float fogColorGreen;
    @Shadow private float fogColorBlue;

    @ModifyConstant(method = "updateRenderer", constant = @Constant(intValue = 3))
    private int modifyRD(int constant) {
        return this.mc.gameSettings.getRenderDistance() * 2;
    }
    @ModifyConstant(method = "updateRenderer", constant = @Constant(floatValue = 3.0F))
    private float modifyRD_1(float constant) {
        return 16;
    }

    @Inject(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glLoadIdentity()V", shift = At.Shift.AFTER))
    private void setupCameraTransform0(float par1, int par2, boolean extend_far_clipping_plane, CallbackInfo ci) {
        this.farPlaneDistance = this.mc.gameSettings.getRenderDistance() * 16;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/ClippingHelperImpl;getInstance()Lnet/minecraft/ClippingHelper;"))
    private void renderWorld(float par1, long par2, CallbackInfo ci) {
        RenderGlobal renderGlobal = this.mc.renderGlobal;
        if ((this.mc.gameSettings.getRenderDistance() >= 4)) {
            setupFog(-1, par1);
            this.mc.mcProfiler.endStartSection("sky");
            renderGlobal.renderSky(par1);
        }
    }

    @Inject(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/WorldClient;getRainStrength(F)F"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void updateFogColor(float par1, CallbackInfo ci, WorldClient var2, EntityLivingBase var3, float var4, Vec3 var5, float var6, float var7, float var8) {
        float var4_1 = 1.0f - ((float) Math.pow(0.25f + ((0.75f * this.mc.gameSettings.getRenderDistance()) / 32.0f), 0.25d));
        this.fogColorRed += (var6 - this.fogColorRed) * var4_1;
        this.fogColorGreen += (var7 - this.fogColorGreen) * var4_1;
        this.fogColorBlue += (var8 - this.fogColorBlue) * var4_1;
    }

    @ModifyExpressionValue(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;getRenderDistance()I"))
    private int modifyRenderDistance(int original) {
        if (original >= 4)
            return 1;
        return original;
    }

    /**
     * @author Xy_Lose
     * @reason break Fps limit
     */
    @Overwrite
    public static int performanceToFps(int par0) {
        if (!(Minecraft.getMinecraft().gameSettings.limitFramerate >= 260)) {
            return Minecraft.getMinecraft().gameSettings.limitFramerate;
        }
        return 9999;
    }

    @WrapOperation(method = "addRainParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/WorldClient;playSound(DDDLjava/lang/String;FFZ)V"))
    private void modifyRainSound(WorldClient instance, double v, double par1, double par3, String par5, float par7Str, float par8, boolean par9, Operation<Void> original) {
        this.mc.theWorld.playSound(v, par1, par3, par5, ((IGameSetting) Minecraft.getMinecraft().gameSettings).getWeatherVolume() * par7Str, par8, par9);
    }

//    @ModifyConstant(method = "getFOVModifier", constant = @Constant(floatValue = 70.0F))
//    private float getFOVModifier(float constant) {
//        return 30.0F;
//    }

//    @Inject(method = "updateFovModifierHand", at = @At("HEAD"), cancellable = true)
//    private void updateFovModifierHand(CallbackInfo ci) {
//        ci.cancel();
//    }

//    @Overwrite
//    private float getFOVModifier(float par1, boolean par2) {
//        if (this.debugViewDirection > 0) {
//            return 90.0F;
//        } else {
//            EntityPlayer var3 = (EntityPlayer) this.mc.renderViewEntity;
//            float var4 = 70.0F;
//
//            if (par2) {
//                var4 *= this.mc.gameSettings.fovSetting;
//                var4 *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * par1;
//            }
//
//            if (var3.getHealth() <= 0.0F) {
//                float var5 = (float) var3.deathTime + par1;
//                var4 /= (1.0F - 500.0F / (var5 + 500.0F)) * 2.0F + 1.0F;
//            }
//
//            int var6 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, var3, par1);
//
//            if (var6 != 0 && Block.blocksList[var6].blockMaterial == Material.water) {
//                var4 = var4 * 60.0F / 70.0F;
//            }
//
//            return var4 + this.prevDebugCamFOV + (this.debugCamFOV - this.prevDebugCamFOV) * par1;
//        }
//    }
}
