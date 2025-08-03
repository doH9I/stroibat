package com.construction.crm.config;

import com.construction.crm.entity.Role;
import com.construction.crm.entity.User;
import com.construction.crm.repository.RoleRepository;
import com.construction.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeUsers();
    }
    
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role(Role.RoleName.ROLE_ADMIN, "Administrator with full access");
            Role managerRole = new Role(Role.RoleName.ROLE_MANAGER, "Project manager with management access");
            Role engineerRole = new Role(Role.RoleName.ROLE_ENGINEER, "Engineer with technical access");
            Role workerRole = new Role(Role.RoleName.ROLE_WORKER, "Worker with basic access");
            Role accountantRole = new Role(Role.RoleName.ROLE_ACCOUNTANT, "Accountant with financial access");
            Role clientRole = new Role(Role.RoleName.ROLE_CLIENT, "Client with limited access");
            
            roleRepository.save(adminRole);
            roleRepository.save(managerRole);
            roleRepository.save(engineerRole);
            roleRepository.save(workerRole);
            roleRepository.save(accountantRole);
            roleRepository.save(clientRole);
        }
    }
    
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            // Create admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@construction.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPhone("+7-999-123-45-67");
            admin.setPosition("System Administrator");
            admin.setDepartment("IT");
            admin.setHireDate(LocalDateTime.now());
            admin.setEnabled(true);
            
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(roleRepository.findByName(Role.RoleName.ROLE_ADMIN).orElse(null));
            admin.setRoles(adminRoles);
            
            userRepository.save(admin);
            
            // Create manager user
            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@construction.com");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setFirstName("Project");
            manager.setLastName("Manager");
            manager.setPhone("+7-999-234-56-78");
            manager.setPosition("Project Manager");
            manager.setDepartment("Management");
            manager.setHireDate(LocalDateTime.now());
            manager.setEnabled(true);
            
            Set<Role> managerRoles = new HashSet<>();
            managerRoles.add(roleRepository.findByName(Role.RoleName.ROLE_MANAGER).orElse(null));
            manager.setRoles(managerRoles);
            
            userRepository.save(manager);
            
            // Create engineer user
            User engineer = new User();
            engineer.setUsername("engineer");
            engineer.setEmail("engineer@construction.com");
            engineer.setPassword(passwordEncoder.encode("engineer123"));
            engineer.setFirstName("Senior");
            engineer.setLastName("Engineer");
            engineer.setPhone("+7-999-345-67-89");
            engineer.setPosition("Senior Engineer");
            engineer.setDepartment("Engineering");
            engineer.setHireDate(LocalDateTime.now());
            engineer.setEnabled(true);
            
            Set<Role> engineerRoles = new HashSet<>();
            engineerRoles.add(roleRepository.findByName(Role.RoleName.ROLE_ENGINEER).orElse(null));
            engineer.setRoles(engineerRoles);
            
            userRepository.save(engineer);
            
            // Create worker user
            User worker = new User();
            worker.setUsername("worker");
            worker.setEmail("worker@construction.com");
            worker.setPassword(passwordEncoder.encode("worker123"));
            worker.setFirstName("Construction");
            worker.setLastName("Worker");
            worker.setPhone("+7-999-456-78-90");
            worker.setPosition("Construction Worker");
            worker.setDepartment("Construction");
            worker.setHireDate(LocalDateTime.now());
            worker.setEnabled(true);
            
            Set<Role> workerRoles = new HashSet<>();
            workerRoles.add(roleRepository.findByName(Role.RoleName.ROLE_WORKER).orElse(null));
            worker.setRoles(workerRoles);
            
            userRepository.save(worker);
            
            // Create accountant user
            User accountant = new User();
            accountant.setUsername("accountant");
            accountant.setEmail("accountant@construction.com");
            accountant.setPassword(passwordEncoder.encode("accountant123"));
            accountant.setFirstName("Financial");
            accountant.setLastName("Accountant");
            accountant.setPhone("+7-999-567-89-01");
            accountant.setPosition("Senior Accountant");
            accountant.setDepartment("Finance");
            accountant.setHireDate(LocalDateTime.now());
            accountant.setEnabled(true);
            
            Set<Role> accountantRoles = new HashSet<>();
            accountantRoles.add(roleRepository.findByName(Role.RoleName.ROLE_ACCOUNTANT).orElse(null));
            accountant.setRoles(accountantRoles);
            
            userRepository.save(accountant);
        }
    }
}