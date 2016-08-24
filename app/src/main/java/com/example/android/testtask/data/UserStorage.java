package com.example.android.testtask.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class UserStorage {

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_SURNAME = "surname";
    private static final String FIELD_INFO = "info";
    private static final String FIELD_DATE = "created_at";


    private static List<User> sUsers = new LinkedList<>();

    public static List<User> getInstance() {
        return sUsers;
    }

    public static User getUserById(int id) {
        for (User user : sUsers)
            if (user.getId() == id)
                return user;

        return null;
    }

    public static void listFromJSON(String json) {
        sUsers.clear();

        try {
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                int id = object.getInt(FIELD_ID);
                String name = object.getString(FIELD_NAME);
                String surname = object.getString(FIELD_SURNAME);

                User user = new User();
                user.setId(id);
                user.setName(name);
                user.setSurname(surname);
                sUsers.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void updateUserFromJSON(User user, String json) {
        try {
            JSONObject object = new JSONObject(json);

            user.setId(object.getInt(FIELD_ID));
            user.setName(object.getString(FIELD_NAME));
            user.setSurname(object.getString(FIELD_SURNAME));
            user.setInfo(object.getString(FIELD_INFO));
            user.setDate(object.getString(FIELD_DATE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
