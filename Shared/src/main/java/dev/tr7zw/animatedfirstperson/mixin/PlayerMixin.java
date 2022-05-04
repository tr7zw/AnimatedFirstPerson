package dev.tr7zw.animatedfirstperson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.animatedfirstperson.AnimatedFirstPersonShared;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void animationUpdate(CallbackInfo info) {
        if((Object)this instanceof LocalPlayer && Minecraft.getInstance().cameraEntity instanceof AbstractClientPlayer cameraPlayer) {
            AnimatedFirstPersonShared.animationManager.tick(cameraPlayer);
        }
    }
    
}
