package io.github.moulberry.insomnia.mixins;

import io.github.moulberry.insomnia.Insomnia;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {

    @Inject(method="getEntityTexture", at=@At("HEAD"), cancellable = true)
    public void getEntityTexture(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> cir) {
        if(!player.hasSkin()) {
            return;
        }

        ResourceLocation resourceLocation = player.getLocationSkin();
        if(Insomnia.isDreamSkin(player.getUniqueID(), resourceLocation)) {
            cir.setReturnValue(DefaultPlayerSkin.getDefaultSkin(player.getUniqueID()));
        } else {
            cir.setReturnValue(resourceLocation);
        }
    }

}
