package io.github.grupo01.volve_a_casa.config;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
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
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PetRepository petRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = initializeAdminUser();
        if (admin != null) {
            initializeSamplePet(admin);
        }
    }

    private User initializeAdminUser() {
        String adminEmail = "admin@volveacasa.com";
        
        if (userRepository.existsByEmail(adminEmail)) {
            logger.info("Usuario administrador ya existe: {}", adminEmail);
            return userRepository.findByEmail(adminEmail).orElse(null);
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
            
            User savedAdmin = userRepository.save(admin);
            
            logger.info("Usuario administrador creado exitosamente");
            
            return savedAdmin;
            
        } catch (Exception e) {
            logger.error("Error al crear usuario administrador: {}", e.getMessage());
            return null;
        }
    }

    private void initializeSamplePet(User creator) {
        try {
            // Verificar si ya existe una mascota de ejemplo
            if (petRepository.count() > 0) {
                logger.info("Ya existen mascotas en la base de datos");
                return;
            }

            // URL de imagen placeholder para un perro
            String photoUrl = "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=400";

            Pet samplePet = new Pet(
                    "Max",
                    Pet.Size.MEDIANO,
                    "Perro labrador color dorado, muy amigable y juguetón. Tiene una mancha blanca en el pecho. Responde a su nombre y le encanta jugar con pelotas.",
                    "Dorado",
                    "Labrador Retriever",
                    25.5f,
                    -34.9214f,  // Latitud cerca de La Plata
                    -57.9544f,  // Longitud cerca de La Plata
                    Pet.Type.PERRO,
                    Pet.State.PERDIDO_PROPIO,
                    creator,
                    photoUrl
            );

            petRepository.save(samplePet);
            
            logger.info("Mascota de ejemplo creada exitosamente: {}", samplePet.getName());
            
        } catch (Exception e) {
            logger.error("Error al crear mascota de ejemplo: {}", e.getMessage());
        }
    }
}

