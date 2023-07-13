package dev.wateralt.mc.bridgeforge;

import com.google.gson.Gson;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("bridgeforge")
public class BridgeForge
{
    MinecraftServer server;
    Socket socket;
    Thread socketThread;
    Gson gson = new Gson();
    Config cfg = new Config();
    Logger log = LogManager.getLogger(BridgeForge.class.getName());

    public BridgeForge()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
        socketThread = new Thread(this::threadMain);
        socketThread.start();
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) throws IOException {
        sendMsg(new Packets.BDMessage(
            cfg.serverName.get(),
            event.getPlayer().getName().toString(),
            event.getMessage()
        ));
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) throws IOException {
        sendMsg(new Packets.BDMessage(
            cfg.serverName.get(),
            "sys",
            "%s joined the game".formatted(event.getEntity().getName().getString())
        ));
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) throws IOException {
        sendMsg(new Packets.BDMessage(
            cfg.serverName.get(),
            "sys",
            "%s left the game".formatted(event.getEntity().getName().getString())
        ));
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) throws IOException {
        if(event.getEntity() instanceof Player) {
            sendMsg(new Packets.BDMessage(
                cfg.serverName.get(),
                "sys",
                event.getSource().getLocalizedDeathMessage(event.getEntityLiving()).getString()
            ));
        }
    }

    private void threadMain() {
        while(true) {
            try {
                socket = new Socket(cfg.host.get(), cfg.port.get());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                while(true) {
                    Packets.Packet packet = gson.fromJson(reader.readLine(), Packets.Packet.class);
                    if(packet.bdMessage != null) {
                        Packets.BDMessage msg = packet.bdMessage;
                        server.getPlayerList().broadcastMessage(
                            new TextComponent(msg.toString()),
                            ChatType.SYSTEM,
                            new UUID(727, 727)
                        );
                    }
                }
            } catch(Exception err) {
                log.warn(err);
                try {
                    if(socket != null) {
                        socket.close();
                    }
                } catch(IOException ignored2) {
                }
                socket = null;
            }
            try {
                Thread.sleep(5000);
            } catch(InterruptedException ignored) {

            }
        }
    }

    private void sendMsg(Packets.BDMessage message) throws IOException {
        if(socket != null) {
            Packets.Packet packet = new Packets.Packet();
            packet.bdMessage = message;
            byte[] bytes = (gson.toJson(packet) + "\n")
                .getBytes(StandardCharsets.UTF_8);
            socket.getOutputStream().write(bytes);
        }
    }
}
