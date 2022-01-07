package com.example.community.utils;

import com.example.community.domain.User;
import org.springframework.stereotype.Component;



@Component
public class HostHolder {
    private ThreadLocal<User> t=new ThreadLocal<>();

    public User getUser() {
        return t.get();
    }

    public void setUser(User user) {
        t.set(user);
    }

    public void remove(){
        t.remove();
    }

}
