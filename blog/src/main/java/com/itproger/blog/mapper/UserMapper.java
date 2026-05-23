package com.itproger.blog.mapper;

import java.util.stream.Collectors;

import com.itproger.blog.dto.UserDto;
import com.itproger.blog.dto.UserLoggedDto;
import com.itproger.blog.models.Permission;
import com.itproger.blog.models.User;

public class UserMapper {
    
    public static UserDto userToUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getRole() != null ? user.getRole().getAuthority() : null,
            user.getRole() != null && user.getRole().getPermissions() != null ? 
                user.getRole().getPermissions().stream()
                    .map(Permission::getAuthority)
                    .collect(Collectors.toSet()) : null
        );
    }
    
    public static User userDtoToUser(UserDto dto) {
        User user = new User();
        user.setUsername(dto.username());
        return user;
    }
    
    public static UserLoggedDto userToUserLoggedDto(User user) {
        return new UserLoggedDto(
            user.getUsername(),
            user.getRole() != null ? user.getRole().getAuthority() : null,
            user.getRole() != null && user.getRole().getPermissions() != null ?
                user.getRole().getPermissions().stream()
                    .map(Permission::getAuthority)
                    .collect(Collectors.toSet()) : null
        );
    }
}