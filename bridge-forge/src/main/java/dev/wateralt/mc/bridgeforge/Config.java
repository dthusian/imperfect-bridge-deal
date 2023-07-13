package dev.wateralt.mc.bridgeforge;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public final ForgeConfigSpec.ConfigValue<String> host;
    public final ForgeConfigSpec.IntValue port;
    public final ForgeConfigSpec.ConfigValue<String> serverName;

    public Config() {
        ForgeConfigSpec.Builder cfg = new ForgeConfigSpec.Builder();
        host = cfg
            .comment("IP of the bridge server")
            .define("server.host", "127.0.0.1");
        port = cfg
            .comment("Port of the bridge server")
            .defineInRange("server.port", 8555, 0, 65535);
        serverName = cfg
            .comment("Name of the bridge source")
            .define("server.name", "mc");
        ForgeConfigSpec spec = cfg.build();
    }
}
