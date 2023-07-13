import { Client, GatewayIntentBits } from "discord.js";
import { EventEmitter } from "events";
import { stringifyMessage } from "./util.mjs";

export class DiscordBridge extends EventEmitter {
  constructor(token, channelId) {
    super();
    this.bot = new Client({ intents: [GatewayIntentBits.Guilds, GatewayIntentBits.GuildMessages, GatewayIntentBits.MessageContent] });

    this.bot.on("ready", () => {
      console.log("Connected to Discord");
    });
  
    this.bot.on("messageCreate", msg => {
      if(msg.channelId === channelId && msg.author.id != this.bot.user.id) {
        this.emit("message", {
          source: "dscd",
          author: msg.author.username,
          content: msg.content
        });
      }
    });

    this.bot.on("error", err => {
      console.log("error: " + err);
    });

    this.bot.login(token);
    this.channelId = channelId;
  }

  async send(msg) {
    let channel = await this.bot.channels.fetch(this.channelId);
    channel.send({
      content: stringifyMessage(msg),
      allowedMentions: {}
    });
  }
}