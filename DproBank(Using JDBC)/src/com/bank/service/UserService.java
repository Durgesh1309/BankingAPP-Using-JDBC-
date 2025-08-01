package com.bank.service;
import com.bank.db.UserDatabase;
import com.bank.model.User;

public class UserService {
    private final UserDatabase userDb = new UserDatabase();

    public int registerUser(String name, String mobile) {
        User existingUser = userDb.findUserByMobile(mobile);
        if (existingUser != null) {
            return existingUser.getUserId(); 
        }
        User newUser = new User();
        newUser.setName(name);
        newUser.setMobile(mobile);
        return userDb.createUser(newUser);
    }

    public User getUserByMobile(String mobile) {
        return userDb.findUserByMobile(mobile);
    }

    public User getUserById(int userId) {
        return userDb.findUserById(userId);
    }
 
    public boolean updateUserName(int userId, String newName) {
        return userDb.updateUserName(userId, newName);
    }

    public boolean updateUserMobile(int userId, String newMobile) {
        return userDb.updateUserMobile(userId, newMobile);
    }

}
