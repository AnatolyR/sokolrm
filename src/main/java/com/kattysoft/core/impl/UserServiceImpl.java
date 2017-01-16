/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.impl;

import com.kattysoft.core.UserService;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.User;
import com.kattysoft.core.repository.UserRepository;
import com.kattysoft.core.specification.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 15.12.2016
 */
public class UserServiceImpl implements UserService {
    public static final String PASS_SALT = "_sdf345sf34";

    @Autowired
    private UserRepository userRepository;

    private Map<Long, User> usersPerThread = new ConcurrentHashMap<>();

    @Override
    public List<User> getUsersByShortTitle(String title) {
        List<User> users = userRepository.findByTitleContaining(title);
        return users;
    }

    @Override
    public String getUserTitleById(String userId) {
        User user = userRepository.findOne(UUID.fromString(userId));
        return user != null ? user.getTitle() : null;
    }

    @Override
    public User getUserByLoginAndPassword(String login, String password) {
        try {
            String hashedPass = md5(md5(md5(password) + login) + PASS_SALT);
            User user = userRepository.findByLoginAndPassword(login, hashedPass);
            return user;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Can not get user by login and pass", e);
        }
    }

    public Page<User> getUsers(Specification specification) {
        int offset = specification.getOffset();
        int size = specification.getSize();
        int pageNum = offset / size;

        Sort sort = new Sort("lastName");
        PageRequest pageRequest = new PageRequest(pageNum, size, sort);
        org.springframework.data.domain.Page<User> repoPage = userRepository.findAll(pageRequest);

        Page<User> page = new Page<>(repoPage.getTotalElements(), repoPage.getContent());
        return page;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static String md5(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] md5s = MessageDigest.getInstance("MD5").digest(input.getBytes("utf-8"));
        String output = byteArrayToHex(md5s);
        return output;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    @Override
    public void setCurrentUser(User user) {
        long id = Thread.currentThread().getId();
        if (user != null) {
            usersPerThread.put(id, user);
        } else {
            usersPerThread.remove(id);
        }
    }

    @Override
    public User getCurrentUser() {
        long id = Thread.currentThread().getId();
        User user = usersPerThread.get(id);
        return user;
    }
}
