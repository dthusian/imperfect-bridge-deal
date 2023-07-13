import { BridgeCore } from "./core.mjs";
import { DiscordBridge } from "./discord.mjs";
import { createTcpServer } from "./tcp.mjs";


let DISCORD_TOKEN = process.env["DISCORD_TOKEN"];
let DISCORD_CHANNEL = process.env["DISCORD_CHANNEL"];

let core = new BridgeCore();

core.add(new DiscordBridge(DISCORD_TOKEN, DISCORD_CHANNEL));

createTcpServer(8555, core);