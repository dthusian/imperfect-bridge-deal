export function stringifyMessage(msg) {
  return `<${msg.source || "unknown-source"} ${msg.author}> ${msg.content || ""}`;
}