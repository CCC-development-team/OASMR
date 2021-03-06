/*
 * Copyright (c) 2019. JaaJ-dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.jaaj.oasmr.supervisor.auth;

import com.google.gson.Gson;
import dev.jaaj.oasmr.supervisor.auth.exception.ExceptionLoginAlreadyExisting;
import dev.jaaj.oasmr.supervisor.auth.exception.ExceptionUserUnknown;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UserList {
    private HashSet<User> userList;

    public UserList(){
        userList = new HashSet<>();
    }

    public void addUser(User newUser) throws ExceptionLoginAlreadyExisting {
        for (User user : userList) {
            if (user.getLogin().equals(newUser.getLogin())){
                throw new ExceptionLoginAlreadyExisting(newUser.getLogin()+ ": login already used");
            }
        }
        userList.add(newUser);
        try {
            saveUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modifyUserLogin(String login, String newLogin) throws ExceptionLoginAlreadyExisting, ExceptionUserUnknown {
        for (User user : userList) {
            if (user.getLogin().equals(newLogin)){
                throw new ExceptionLoginAlreadyExisting(newLogin + ": login already used");
            }
        }
        getUser(login).setLogin(newLogin);
        try {
            saveUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modifyUserPassword(String login, String password, String newPassword) throws ExceptionUserUnknown {
        User oldUser = new User(login, password);
        if (!authenticate(oldUser.getLogin(),oldUser.getPassword())){
            throw new ExceptionUserUnknown(oldUser.getLogin()+ ": incorrect password)");
        }
        getUser(login).setPassword(newPassword);
        try {
            saveUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(User user2delete) throws ExceptionUserUnknown {
        if (!authenticate(user2delete.getLogin(),user2delete.getPassword())){
            throw new ExceptionUserUnknown(user2delete.getLogin() + ": incorrect user (login or password)");
        }
        for (Iterator<User> it = userList.iterator(); it.hasNext();){
            User u = it.next();
            if (u.equals(user2delete)){
                it.remove();
                break;
            }
        }
        try {
            saveUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(String login, String passwordHashed){
        for (User user : userList) {
            if (user.getLogin().equals(login)) {
                if (user.getPassword().equals(passwordHashed)){
                    user.authenticate();
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public User getUser(String login) throws ExceptionUserUnknown {
        for (User user : userList) {
            if (user.getLogin().equals(login)){
                return user;
            }
        }
        throw new ExceptionUserUnknown(login + "not found");
    }

    public boolean isAuthenticate(String login){
        for (User user : userList) {
            if (user.getLogin().equals(login)){
                return user.getAuthentication();
            }
        }
        return false;
    }

    public List<String> getLoginList(){
        List<String> loginList = new ArrayList<>();
        for (User user : userList) {
            loginList.add(user.getLogin());
        }
        return loginList;
    }

    public void saveUsers() throws IOException {
        Gson gson = new Gson();
        String users2Json = gson.toJson(userList.toArray(new User[0]));
        FileWriter file = new FileWriter("users.json");
        file.write(users2Json);
        file.close();
    }

    public void loadUsers() throws IOException {
        Gson gson = new Gson();
        byte[] encoded = Files.readAllBytes(Paths.get("users.json"));
        String users2Json = new String(encoded, "UTF-8");
        User[] users = gson.fromJson(users2Json, User[].class);
        userList.addAll(Arrays.asList(users));
    }
}
