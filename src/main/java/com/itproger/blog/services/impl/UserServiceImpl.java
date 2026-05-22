package com.itproger.blog.services.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.itproger.blog.dto.UserDto;
import com.itproger.blog.exception.AppException;
import com.itproger.blog.exception.ResourceNotFoundException;
import com.itproger.blog.mapper.UserMapper;
import com.itproger.blog.models.Role;
import com.itproger.blog.models.User;
import com.itproger.blog.repo.RoleRepository;
import com.itproger.blog.repo.UserRepository;
import com.itproger.blog.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
            .map(UserMapper::userToUserDto).toList();
    }
    
    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.userDtoToUser(userDto);

        Role role = roleRepository.findByName(userDto.role())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(userDto.password()));

        return UserMapper.userToUserDto(userRepository.save(user));
    }
    
    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
        return UserMapper.userToUserDto(user);
    }
    
    @Override
    public UserDto getUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
        return UserMapper.userToUserDto(user);
    }
    
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        Role role = roleRepository.findByName(userDto.role())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRole(role);

        return UserMapper.userToUserDto(userRepository.save(user));
    }
    
    @Override
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
        return String.format("User with %d deleted successfully", userId);
    }
}