package com.lethimyhoa.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListUserAccount {
    public static ArrayList<UserAccount> listUserAccounts()
    {
        ArrayList<UserAccount> list=new ArrayList<>();
        list.add(new UserAccount("admin","123","admin","Admin",true));
        list.add(new UserAccount("user1","user1","Reporter","Trần Minh Nhựt",true));
        list.add(new UserAccount("user2","user2","Technical","Phạm Văn Tý",true));
        return list;
    }

    public static UserAccount login(String username, String password)
    {
        UserAccount uc=null;
        //get sample data
        ArrayList<UserAccount> list=listUserAccounts();
        //check for login
        for(UserAccount u:list)
        {
            if(u.getUsername().equals(username) && u.getPassword().equals(password))
            {
                uc=u;
                break;
            }
        }
        return uc;
    }

}
