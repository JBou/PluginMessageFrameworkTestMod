package com.example.examplemod;

import com.ikeirnez.pluginmessageframework.forge.ForgeGatewayProvider;
import com.ikeirnez.pluginmessageframework.gateway.ClientGateway;
import com.ikeirnez.pluginmessageframework.gateway.ServerGateway;
import com.ikeirnez.pluginmessageframework.packet.PacketHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import pmf.MyPacket;

/**
 * @author JBou
 */
@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "pmftestmod";
    public static final String VERSION = "@MOD_VERSION@";

    public ClientGateway clientGateway;
    public ServerGateway<EntityPlayerMP> serverGateway;
    public KeyBinding sendPacket;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            FMLCommonHandler.instance().bus().register(new KeyInputHandler());
            sendPacket = new KeyBinding("key.sendPacket", Keyboard.KEY_P, "key.categories.pmf");
            ClientRegistry.registerKeyBinding(sendPacket);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            clientGateway = ForgeGatewayProvider.getClientGateway("MyChannelName");
            clientGateway.registerListener(this);
        } else {
            serverGateway = ForgeGatewayProvider.getServerGateway("MyChannelName");
            serverGateway.registerListener(this);
        }
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new TestCommand());
    }

    @PacketHandler
    public void onMyPacket(MyPacket myPacket) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            System.out.println("[CLIENT] Received message: " + myPacket.getMessage());
        } else {
            System.out.println("[SERVER] Received message: " + myPacket.getMessage());
        }
    }

    public class KeyInputHandler {
        @SubscribeEvent
        public void onKeyInput(InputEvent.KeyInputEvent event) {
            if (sendPacket.isPressed()) {
                clientGateway.sendPacket(new MyPacket("send from client"));
            }
        }
    }

    public class TestCommand extends CommandBase {
        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            return true;
        }

        @Override
        public String getCommandName() {
            return "test";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "test";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException {
            if (sender instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) sender;
                serverGateway.sendPacket(player, new MyPacket("send from server: " + StringUtils.join(args, " "))); // send a packet containing the arguments used in the command
            }
        }
    }

}
