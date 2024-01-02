package com.karag.onlinechatapp.model;

public class ChatMessage {
    public String chat_id;
    public String sender_id;
    public String text;
    public long timestamp;

    public ChatMessage() {
    }
    public ChatMessage(String chat_id,String sender_id, String text) {
        this.chat_id = chat_id;
        this.sender_id = sender_id;
        this.text = text;
       this.timestamp=System.currentTimeMillis();
      }
}
