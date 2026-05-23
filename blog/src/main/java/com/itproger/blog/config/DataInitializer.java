package com.itproger.blog.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itproger.blog.enums.Roles;
import com.itproger.blog.models.Permission;
import com.itproger.blog.models.Role;
import com.itproger.blog.models.User;
import com.itproger.blog.repo.PermissionRepository;
import com.itproger.blog.repo.RoleRepository;
import com.itproger.blog.repo.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
    
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("🚀 Начинаем инициализацию данных...");
        
        createPermissionsIfNotExists();
        createRolesIfNotExists();
        createUsersIfNotExists();
        
        System.out.println("✅ Инициализация данных завершена!");
    }
    
    private void createUsersIfNotExists() {
        if (userRepository.count() > 0) {
            System.out.println("👥 Пользователи уже существуют, пропускаем...");
            return;
        }

        Role roleAdmin = roleRepository.findByName(Roles.ADMIN.name())
            .orElseThrow(() -> new RuntimeException("ADMIN role not found!"));
        Role roleUser = roleRepository.findByName(Roles.USER.name())
            .orElseThrow(() -> new RuntimeException("USER role not found!"));

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(roleAdmin)
                .build();
        userRepository.save(admin);
        
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .role(roleUser)
                .build();
        userRepository.save(user);
        
        System.out.println("✅ Созданы пользователи: admin/admin и user/user");
    }
    
    private void createRolesIfNotExists() {
        if (roleRepository.count() > 0) {
            System.out.println("👥 Роли уже существуют, пропускаем...");
            return;
        }
        
        // Получаем разрешения
        Permission postRead = permissionRepository.findByResourceAndOperation("post", "read")
            .orElseThrow(() -> new RuntimeException("Permission not found"));
        Permission postWrite = permissionRepository.findByResourceAndOperation("post", "write")
            .orElseThrow(() -> new RuntimeException("Permission not found"));
        Permission postDelete = permissionRepository.findByResourceAndOperation("post", "delete")
            .orElseThrow(() -> new RuntimeException("Permission not found"));
        
        // Создаём роли
        Role userRole = Role.builder()
            .name(Roles.USER.name())
            .build();
        
        Role adminRole = Role.builder()
            .name(Roles.ADMIN.name())
            .build();

        roleRepository.save(userRole);
        roleRepository.save(adminRole);

        // Назначаем разрешения (создаём новые Set, чтобы избежать LazyInitialization)
        Set<Permission> userPermissions = new HashSet<>();
        userPermissions.add(postRead);
        userRole.setPermissions(userPermissions);
        
        Set<Permission> adminPermissions = new HashSet<>();
        adminPermissions.add(postRead);
        adminPermissions.add(postWrite);
        adminPermissions.add(postDelete);
        adminRole.setPermissions(adminPermissions);

        roleRepository.save(userRole);
        roleRepository.save(adminRole);
        
        System.out.println("✅ Созданы роли: USER, ADMIN");
    }
    
    private void createPermissionsIfNotExists() {
        if (permissionRepository.count() > 0) {
            System.out.println("🔑 Разрешения уже существуют, пропускаем...");
            return;
        }
        
        Permission read = new Permission("post", "read");
        Permission write = new Permission("post", "write");
        Permission delete = new Permission("post", "delete");
        
        permissionRepository.save(read);
        permissionRepository.save(write);
        permissionRepository.save(delete);
        
        System.out.println("✅ Созданы разрешения для постов");
    }
}