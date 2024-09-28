package com.yoonji.adminproject.init;

import com.yoonji.adminproject.user.entity.Role;
import com.yoonji.adminproject.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            List<Role> roles = Arrays.asList(
                    new Role("ROLE_USER"),
                    new Role("ROLE_ADMIN"),
                    new Role("ROLE_MANAGER")
            );
            roleRepository.saveAll(roles);
        }
    }
}
