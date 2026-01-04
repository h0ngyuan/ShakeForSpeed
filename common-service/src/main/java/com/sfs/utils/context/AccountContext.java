package com.sfs.utils.context;

import com.sfs.model.entity.User;
import com.sfs.model.entity.UserAccount;

public class AccountContext {

    public static final ThreadLocal<UserAccount> threadLocal=new ThreadLocal<>();

    public static void setAccount(UserAccount user){
        threadLocal.set(user);
    }
    public static UserAccount getAccount(){return threadLocal.get();};
    public static void clear(){
        threadLocal.remove();
    }
}