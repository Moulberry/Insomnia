package io.github.moulberry.insomnia;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import io.github.moulberry.insomnia.mixins.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(modid = Insomnia.MODID, version = Insomnia.VERSION, clientSideOnly = true)
public class Insomnia {
    public static final String MODID = "insomnia";
    public static final String VERSION = "1.0-REL";

    public static boolean enabled = true;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        ClientCommandHandler.instance.registerCommand(new SimpleCommand("insomnia", new SimpleCommand.ProcessCommandRunnable() {
            @Override
            public void processCommand(ICommandSender sender, String[] args) {
                if(args.length != 1) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid usage: /insomnia on/off"));
                    return;
                }
                if(args[0].equalsIgnoreCase("on")) {
                    enabled = true;
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN+"Removing dream skins..."));
                } else if(args[0].equalsIgnoreCase("off")) {
                    enabled = false;
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN+"Enabling dream skins..."));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Invalid usage: /insomnia on/off"));
                }
            }
        }));
    }

    @SubscribeEvent
    public void onWorldSwitch(WorldEvent.Unload event) {
        dreamSkins.clear();
    }

    public static boolean isDreamSkin(UUID uuid, ResourceLocation resourceLocation) {
        if(!enabled) return false;

        if(dreamSkins.containsKey(uuid)) return dreamSkins.get(uuid);

        if(!resourceLocation.getResourcePath().contains("/")) return false;

        String profileTexture = resourceLocation.getResourcePath().split("/")[1];

        File skinCacheDir = new File(((MinecraftAccessor)Minecraft.getMinecraft()).getFileAssets(), "skins");
        File file1 = new File(skinCacheDir, profileTexture.length() > 2 ? profileTexture.substring(0, 2) : "xx");
        File file2 = new File(file1, profileTexture);

        if(!file2.exists()) return false;

        try(InputStream is = new FileInputStream(file2)) {
            BufferedImage image = ImageIO.read(is);

            int totalPixels = 0;
            int totalNeonGreen = 0;
            for(int x=0; x<image.getWidth(); x++) {
                for(int y=0; y<image.getHeight(); y++) {
                    int argb = image.getRGB(x, y);
                    Color c = new Color(argb, true);

                    if(c.getAlpha() > 20) {
                        int red = c.getRed();
                        int green = c.getGreen();
                        int blue = c.getBlue();

                        if(red == 0 && green == 0 && blue == 0) continue;

                        totalPixels++;

                        if(red >= 0 && red <= 140) {
                            if(green >= 220 && green <= 260) {
                                if(blue >= 0 && blue <= 140) {
                                    totalNeonGreen++;
                                }
                            }
                        }
                    }
                }
            }
            if(totalNeonGreen/(float)totalPixels > 0.7) {
                dreamSkins.put(uuid, true);
                return true;
            }

        } catch(IOException ignored) {
            return false;
        }
        dreamSkins.put(uuid, false);
        return false;
    }


    private static HashMap<UUID, Boolean> dreamSkins = new HashMap<>();

}
