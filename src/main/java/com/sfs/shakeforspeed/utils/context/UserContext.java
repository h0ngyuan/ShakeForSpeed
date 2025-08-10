package com.sfs.shakeforspeed.utils.context;


import com.sfs.shakeforspeed.model.entity.User;

public class UserContext {

    public static final ThreadLocal<User> threadLocal=new ThreadLocal<>();

    public static void setUser(User user){
        threadLocal.set(user);
    }
    public static User getUser(){return threadLocal.get();};
    public static void clear(){
        threadLocal.remove();
    }


}
