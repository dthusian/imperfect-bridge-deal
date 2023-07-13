import { stringifyMessage } from "./util.mjs";

export class BridgeCore {
  constructor() {
    this.bridges = new Set();
  }

  add(bridge) {
    this.bridges.add(bridge);
    bridge.on("message", msg => {
      console.log(stringifyMessage(msg));
      this.bridges.forEach(v => {
        if(v !== bridge) {
          v.send(msg);
        }
      });
    });
  }

  delete(bridge) {
    this.bridges.delete(bridge);
  }
}