package dev.tr7zw.animatedfirstperson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.animatedfirstperson.AnimationResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    // FIXME
    @Inject(method = "createSearchTrees", at = @At("HEAD"))
    private void createSearchTrees(CallbackInfo ci) {
        ((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(new AnimationResourceLoader());
    }
    
}
