package com.demo.service.impl;

import com.demo.model.User;
import com.demo.repository.UserRepository;
import com.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getUserInfo() {
        return userRepository.getUsers();
    }
}
