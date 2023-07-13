import { EventEmitter } from "events";
import { createServer } from "net";
import { createInterface } from "readline";
import { createWriteStream } from "fs";

export class TcpSocketBridge extends EventEmitter {
  constructor(socket) {
    super();
    this.socket = socket;

    let rl = createInterface({
      input: socket,
      output: createWriteStream('/dev/null'),
      terminal: false
    });
  
    rl.on("line", async line => {
      let json;
      try {
        json = JSON.parse(line);
      } catch(e) {
        socket.destroy();
      }
      try {
        if(json.bdMessage) {
          this.emit("message", json.bdMessage);
        }
      } catch(e) { }
    });
  }

  send(msg) {
    this.socket.write(JSON.stringify({ bdMessage: msg }) + "\n");
  }
}

export function createTcpServer(port, core) {
  createServer(socket => {
    console.log(`Connected to ${socket.remoteAddress}:${socket.remotePort}`);
    core.add(new TcpSocketBridge(socket));
  
    socket.on("close", () => {
      console.log(`Disconnected from to ${socket.remoteAddress}:${socket.remotePort}`);
      core.bridges.forEach(v => {
        if(v.socket === socket) {
          core.delete(v);
        }
      })
    });

    socket.on("error", _ => {
      socket.destroy();
    });
  }).listen(port);
}
