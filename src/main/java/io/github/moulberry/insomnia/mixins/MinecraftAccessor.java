package io.github.moulberry.insomnia.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Accessor
    public File getFileAssets();

}
