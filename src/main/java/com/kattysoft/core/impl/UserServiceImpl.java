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

import com.kattysoft.core.*;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.User;
import com.kattysoft.core.repository.UserRepository;
import com.kattysoft.core.specification.SortOrder;
import com.kattysoft.core.specification.Specification;
import com.kattysoft.core.specification.SpecificationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    @Autowired
    private AccessRightService accessRightService;

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

        Sort sort;
        if (specification.getSort() != null && specification.getSort().size() > 0) {
            sort = new Sort(specification.getSort().get(0).getOrder() == SortOrder.ASC
                ? Sort.Direction.ASC : Sort.Direction.DESC, specification.getSort().get(0).getField());
        } else {
            sort = new Sort("lastName");
        }

        PageRequest pageRequest = new PageRequest(pageNum, size, sort);

        org.springframework.data.jpa.domain.Specification<User> spec = null;
        if (specification.getCondition() != null) {
            spec = SpecificationUtil.conditionToSpringSpecification(specification.getCondition(), User.class);
        }

        org.springframework.data.domain.Page<User> repoPage = userRepository.findAll(spec, pageRequest);

        Page<User> page = new Page<>(repoPage.getTotalElements(), repoPage.getContent());
        return page;
    }

    public User getUserById(String id) {
        UUID uuid = UUID.fromString(id);
        User user = userRepository.findOne(uuid);
        return user;
    }

    public String saveUser(User user) {
        if (user.getId() == null) {
            if (!accessRightService.checkRights("_system", "users", "", AccessRightLevel.CREATE)) {
                throw new NoAccessRightsException("No rights to create user");
            }
            UUID id = UUID.randomUUID();
            user.setId(id);
        } else {
            if (!accessRightService.checkRights("_system", "users", "", AccessRightLevel.WRITE)) {
                throw new NoAccessRightsException("No rights to save user");
            }
            User existUser = userRepository.findOne(user.getId());
            if (existUser == null) {
                throw new SokolException("User not found");
            } else {
                BeanUtils.copyProperties(user, existUser, Utils.getNullPropertyNames(user));
                user = existUser;
            }
        }
        if (user.getLogin() != null && !user.getLogin().isEmpty()) {
            List<User> allByLogin = userRepository.findAllByLogin(user.getLogin());
            if (allByLogin != null && allByLogin.size() > 0 && !allByLogin.get(0).getId().equals(user.getId())) {
                throw new SokolException("User with such login already exist");
            }
        }
        userRepository.save(user);
        return user.getId().toString();
    }

    public void deleteUser(String id) {
        if (!accessRightService.checkRights("_system", "users", "", AccessRightLevel.DELETE)) {
            throw new NoAccessRightsException("No rights to delete user");
        }
        UUID uuid = UUID.fromString(id);
        userRepository.delete(uuid);
    }

    public static String chars = "abcdefghigklmopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
    public String resetPassword(String id) {
        int l = chars.length();
        Random random  = new Random();
        String pass = "";
        for (int i = 0; i < 8; i++) {
            pass += chars.charAt(random.nextInt(l));
        }

        savePassword(id, pass);
        return pass;
    }

    public String savePassword(String id, String pass) {
        User user = this.getUserById(id);
        if (user.getLogin() == null || user.getLogin().length() < 3) {
            throw new SokolException("Login length too small");
        }
        try {
            String hashedPass = md5(md5(md5(pass) + user.getLogin()) + PASS_SALT);
            user.setPassword(hashedPass);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.saveUser(user);
        return pass;
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

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
