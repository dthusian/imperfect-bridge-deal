package dev.wateralt.mc.bridgeforge;

public class Packets {
    public static class Packet {
        public BDMessage bdMessage;
    }
    public static class BDMessage {
        public String source;
        public String author;
        public String content;
        public BDMessage(String source, String author, String content) {
            this.source = source;
            this.author = author;
            this.content = content;
        }
        public String toString() {
            return "<%s %s> %s".formatted(this.source, this.author, this.content);
        }

        public String toChatString() {
            return "§b<%s §f%s§b>§f %s".formatted(this.source, this.author, this.content);
        }
    }
}
