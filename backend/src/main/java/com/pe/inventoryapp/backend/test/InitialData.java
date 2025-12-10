package com.pe.inventoryapp.backend.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pe.inventoryapp.backend.company.model.entity.Company;
import com.pe.inventoryapp.backend.company.repository.CompanyRepository;
import com.pe.inventoryapp.backend.organization.model.entity.Location;
import com.pe.inventoryapp.backend.organization.model.entity.Region;
import com.pe.inventoryapp.backend.organization.repository.LocationRepository;
import com.pe.inventoryapp.backend.organization.repository.RegionRepository;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;
import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.RoleRepository;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Configuration
public class InitialData {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private RegionRepository regionRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @PostConstruct
  public void init() {

    // Roles
    Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_USER");
      return roleRepository.save(newRole);
    });

    Role roleOperator = roleRepository.findByName("ROLE_OPERATOR").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_OPERATOR");
      return roleRepository.save(newRole);
    });

    Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_ADMIN");
      return roleRepository.save(newRole);
    });

    // El usuario por defecto (primer usuario de la app)
    if (userRepository.findByEmail("correo@example.com").isEmpty()) {
      List<Role> roles = List.of(roleUser, roleAdmin, roleOperator);

      User user = new User();
      user.setFirstname("Administrador");
      user.setLastname("sin apellidos");
      user.setDni(12345678);
      user.setEmail("correo@example.com");
      user.setPassword(passwordEncoder.encode("12345"));
      user.setRoles(roles);

      userRepository.save(user);
    }

    // La categoria por defecto (representa "sin categoria")
    if (categoryRepository.findByName("Sin categoria").isEmpty()) {
      Category category = new Category();
      category.setName("Sin categoria");
      category.setStatus(true);
      categoryRepository.save(category);
    }

    // La región por defecto (representa "sin región")
    if (regionRepository.findByName("Sin región").isEmpty()) {
      Region region = new Region();
      region.setName("Sin región");
      regionRepository.save(region);
    }

    // La ubicación por defecto (representa "sin ubicación")
    if (locationRepository.findByName("Sin ubicación").isEmpty()) {
      Location location = new Location();
      location.setName("Sin ubicación");
      location.setRegion(regionRepository.findByName("Sin región").get());
      ;
      location.setStatus(true);
      locationRepository.save(location);
    }

    // Una empresa por defecto (representa "propia de la empresa")
    if (companyRepository.findByName("Propia de la empresa").isEmpty()) {
      Company company = new Company();
      company.setName("Propia de la empresa");
      companyRepository.save(company);
    }
  }
}
