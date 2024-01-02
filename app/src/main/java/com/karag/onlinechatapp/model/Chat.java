package com.karag.onlinechatapp.model;

import java.util.List;

public class Chat {
    public String id;
    public List<String> users;

    public Chat(List<String> users) {
        this.users = users;
    }
}
