package com.pe.inventoryapp.backend.start.data;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.model.entity.Region;
import com.pe.inventoryapp.backend.location.model.entity.Subregion;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.location.repository.RegionRepository;
import com.pe.inventoryapp.backend.location.repository.SubregionRepository;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.model.entity.Type;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;
import com.pe.inventoryapp.backend.product.repository.TypeRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
import com.pe.inventoryapp.backend.user.model.data.RoleName;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

// import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Configuration
public class InitialData {
  private final TypeRepository typeRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CategoryRepository categoryRepository;
  private final RegionRepository regionRepository;
  private final SubregionRepository subregionRepository;
  private final LocationRepository locationRepository;
  private final CompanyRepository companyRepository;

  public InitialData(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      CategoryRepository categoryRepository,
      TypeRepository typeRepository,
      RegionRepository regionRepository,
      SubregionRepository subregionRepository,
      LocationRepository locationRepository,
      CompanyRepository companyRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.categoryRepository = categoryRepository;
    this.regionRepository = regionRepository;
    this.subregionRepository = subregionRepository;
    this.locationRepository = locationRepository;
    this.companyRepository = companyRepository;
    this.typeRepository = typeRepository;
  }

  @Value("${first.user.email}")
  private String firstUserEmail;
  @Value("${first.user.password}")
  private String firstUserPassword;
  @Value("${demo.mode}")
  private String demoMode;

  // Metodo que se ejecuta cada vez que se inicia la API REST
  @PostConstruct
  public void init() {
    // Roles
    // Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
    //   Role newRole = new Role();
    //   newRole.setName("ROLE_USER");
    //   newRole.setLabel("Usuario");
    //   return roleRepository.save(newRole);
    // });

    // Role roleOperator = roleRepository.findByName("ROLE_OPERATOR").orElseGet(() -> {
    //   Role newRole = new Role();
    //   newRole.setName("ROLE_OPERATOR");
    //   newRole.setLabel("Operador");
    //   return roleRepository.save(newRole);
    // });

    // Role roleSecretary = roleRepository.findByName("ROLE_SECRETARY").orElseGet(() -> {
    //   Role newRole = new Role();
    //   newRole.setName("ROLE_SECRETARY");
    //   newRole.setLabel("Secretario");
    //   return roleRepository.save(newRole);
    // });

    // Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
    //   Role newRole = new Role();
    //   newRole.setName("ROLE_ADMIN");
    //   newRole.setLabel("Administrador");
    //   return roleRepository.save(newRole);
    // });

    // Region - Subregion - Ubicacion
    Region defaultRegion = regionRepository.findByName("Sin región").orElseGet(() -> {
      Region region = new Region();
      region.setName("Sin región");
      return regionRepository.save(region);
    });

    Subregion defaultSubregion = subregionRepository.findByName("Sin subregión").orElseGet(() -> {
      Subregion subregion = new Subregion();
      subregion.setName("Sin subregión");
      subregion.setRegion(defaultRegion);
      return subregionRepository.save(subregion);
    });

    locationRepository.findByName("Sin ubicación").orElseGet(() -> {
      Location location = new Location();
      location.setName("Sin ubicación");
      location.setAddress("Sin dirección especificada");
      location.setSubregion(defaultSubregion);
      location.setStatus(true);
      return locationRepository.save(location);
    });

    // Dotenv dotenv = Dotenv.load();


    // El usuario por defecto (primer usuario de la app)
    if (userRepository.findById(1L).isEmpty()) {

      // Agregamos todos los roles por orden de jerárquias
      // List<Role> allRoles = new ArrayList<>();
      // allRoles.add(roleUser);
      // allRoles.add(roleOperator);
      // // allRoles.add(roleSecretary);
      // allRoles.add(roleAdmin);

      // System.out.println(allRoles);

      // EL CORREO Y LA CONSTRASEÑA DEL PRIMER USUARIO SE ESTABLECE MEDIANTE UNA VARIABLE DE ENTORNO
      User user = new User();
      user.setFirstname("Primer usuario");
      user.setLastname("del sistema");
      user.setDni(12345678);
      user.setActive(true);

      // user.setEmail(dotenv.get("FIRST_USER_EMAIL"));
      // user.setPassword(passwordEncoder.encode(dotenv.get("FIRST_USER_PASSWORD")));

      user.setEmail(firstUserEmail);
      user.setPassword(passwordEncoder.encode(firstUserPassword));
      // user.setRoles(allRoles);
      user.setRole(RoleName.ROLE_ADMIN);

      userRepository.save(user);
    }

    // La categoria por defecto (representa "sin categoria")
    if (!categoryRepository.existsByName("Sin categoria")) {
      Category category = new Category();
      category.setName("Sin categoria");
      category.setStatus(true);
      categoryRepository.save(category);
    }

    // Una empresa por defecto (representa "propia de la empresa")
    if (!companyRepository.existsByName("Propia de la empresa")) {
      Company company = new Company();
      company.setName("Propia de la empresa");
      companyRepository.save(company);
    }

    if(!typeRepository.existsByName("Sin tipo")) {
      Type type = new Type();
      type.setName("Sin tipo");
      type.setStatus(true);
      typeRepository.save(type);
    }

    // Si esta en modo demo, debe crear 3 usuarios con distintos roles para pruebas
    if (Boolean.parseBoolean(demoMode) == true){
      String testPassword = "12345";

      if (userRepository.findByEmailWithRoles("admin@gmail.com").isEmpty()){
        User userAdmin = new User();
        userAdmin.setFirstname("Administrador");
        userAdmin.setLastname("de prueba");
        userAdmin.setDni(12345678);
        userAdmin.setActive(true);
        userAdmin.setEmail("admin@gmail.com");
        userAdmin.setPassword(passwordEncoder.encode(testPassword));
        userAdmin.setRole(RoleName.ROLE_ADMIN);
        userRepository.save(userAdmin);
      }

      if (userRepository.findByEmailWithRoles("operator@gmail.com").isEmpty()) {
        User userAdmin = new User();
        userAdmin.setFirstname("Operador");
        userAdmin.setLastname("de prueba");
        userAdmin.setDni(12345678);
        userAdmin.setActive(true);
        userAdmin.setEmail("operator@gmail.com");
        userAdmin.setPassword(passwordEncoder.encode(testPassword));
        userAdmin.setRole(RoleName.ROLE_ADMIN);
        userRepository.save(userAdmin);
      }

      if (userRepository.findByEmailWithRoles("user@gmail.com").isEmpty()) {
        User userAdmin = new User();
        userAdmin.setFirstname("Usuario");
        userAdmin.setLastname("de prueba");
        userAdmin.setDni(12345678);
        userAdmin.setActive(true);
        userAdmin.setEmail("user@gmail.com");
        userAdmin.setPassword(passwordEncoder.encode(testPassword));
        userAdmin.setRole(RoleName.ROLE_ADMIN);
        userRepository.save(userAdmin);
      }

    }

    
  }
}
