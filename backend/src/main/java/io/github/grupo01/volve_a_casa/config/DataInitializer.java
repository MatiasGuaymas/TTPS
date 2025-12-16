package io.github.grupo01.volve_a_casa.config;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        String adminEmail = "admin@volveacasa.com";
        
        if (userRepository.existsByEmail(adminEmail)) {
            logger.info("Usuario administrador ya existe: {}", adminEmail);
            return;
        }

        try {
            String hashedPassword = passwordEncoder.encode("admin123");
            
            User admin = new User(
                    "Administrador",
                    "Sistema",
                    adminEmail,
                    hashedPassword,
                    "+54 9 221 000-0000",
                    "La Plata",
                    "Centro",
                    -34.9205f,  // Latitud de La Plata
                    -57.9536f   // Longitud de La Plata
            );
            
            admin.setRole(User.Role.ADMIN);
            
            userRepository.save(admin);
            
            logger.info("Usuario administrador creado exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al crear usuario administrador: {}", e.getMessage());
        }
    }
}
