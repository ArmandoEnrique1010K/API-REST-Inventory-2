package com.pe.inventoryapp.backend.start.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.model.entity.Region;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.location.repository.RegionRepository;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
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

  // Metodo que se ejecuta cada vez que se inicia la API REST
  @PostConstruct
  public void init() {

    // Roles
    Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_USER");
      newRole.setLabel("Usuario");
      return roleRepository.save(newRole);
    });

    Role roleOperator = roleRepository.findByName("ROLE_OPERATOR").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_OPERATOR");
      newRole.setLabel("Operador");
      return roleRepository.save(newRole);
    });

    Role roleSecretary = roleRepository.findByName("ROLE_SECRETARY").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_SECRETARY");
      newRole.setLabel("Secretario");
      return roleRepository.save(newRole);
    });

    Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_ADMIN");
      newRole.setLabel("Administrador");
      return roleRepository.save(newRole);
    });

    // El usuario por defecto (primer usuario de la app)
    if (userRepository.findById(1L).isEmpty()) {

      // Agregamos todos los roles por orden de jerárquias
      List<Role> allRoles = new ArrayList<>();
      allRoles.add(roleUser);
      allRoles.add(roleOperator);
      allRoles.add(roleSecretary);
      allRoles.add(roleAdmin);

      System.out.println(allRoles);

      User user = new User();
      user.setFirstname("Primer usuario");
      user.setLastname("del sistema");
      user.setDni(12345678);
      user.setEmail("correo@example.com");
      user.setActive(true);
      user.setPassword(passwordEncoder.encode("12345"));
      user.setRoles(allRoles);

      userRepository.save(user);
    }

    // La categoria por defecto (representa "sin categoria")
    if (!categoryRepository.existsByName("Sin categoria")) {
      Category category = new Category();
      category.setName("Sin categoria");
      category.setStatus(true);
      categoryRepository.save(category);
    }

    // La región por defecto (representa "sin región")
    if (!regionRepository.existsByName("Sin región")) {
      Region region = new Region();
      region.setName("Sin región");
      regionRepository.save(region);
    }

    // La ubicación por defecto (representa "sin ubicación")
    if (!locationRepository.existsByName("Sin ubicación")) {
      Location location = new Location();
      location.setName("Sin ubicación");
      location.setRegion(regionRepository.findByName("Sin región").get());
      ;
      location.setStatus(true);
      locationRepository.save(location);
    }

    // Una empresa por defecto (representa "propia de la empresa")
    if (!companyRepository.existsByName("Propia de la empresa")) {
      Company company = new Company();
      company.setName("Propia de la empresa");
      companyRepository.save(company);
    }
  }
}
