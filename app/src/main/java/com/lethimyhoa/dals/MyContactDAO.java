package com.lethimyhoa.dals;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.lethimyhoa.models.MyContact;

import java.util.ArrayList;

public class MyContactDAO {
    public static ArrayList<MyContact> getMyContacts(Context context){
        ArrayList<MyContact> contacts = new ArrayList<>();
        Uri uri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor=context.getContentResolver().query(uri,null,null,null,null);
        if (cursor == null) {
            return contacts;
        }
        while(cursor.moveToNext()){
            int nameIndex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            int phoneIndex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String phone=cursor.getString(phoneIndex);
            MyContact myContact=new MyContact(name,phone);
            contacts.add(myContact);
        }
        cursor.close();
        return contacts;
    }
}
