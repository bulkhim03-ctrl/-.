package com.itproger.blog.services;

import java.util.List;

import com.itproger.blog.dto.UserDto;

public interface UserService {
    List<UserDto> getUsers();
    UserDto create(UserDto userDto);
    UserDto getUser(Long userId);
    UserDto getUser(String username);
    UserDto updateUser(Long userId, UserDto userDto);
    String deleteUser(Long userId);
}