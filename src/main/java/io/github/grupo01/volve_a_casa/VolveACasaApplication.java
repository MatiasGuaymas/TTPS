package io.github.grupo01.volve_a_casa;

import org.hibernate.HibernateException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class VolveACasaApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(VolveACasaApplication.class, args);
        } catch (Exception e) {
            if (containsHibernateException(e)) {
                System.out.println("""
                --------------------------------------------------------
                ❌ No se pudo conectar con la base de datos.
                👉 Asegurate de que el contenedor Docker esté corriendo
                y que la base de datos se llame 'volve_a_casa'.

                🔧 Podés iniciarla con:
                    docker compose up -d
                --------------------------------------------------------
                """);
            } else {
                // Si es otro error, lo volvemos a lanzar
                throw e;
            }
        }
    }

    private static boolean containsHibernateException(Throwable e) {
        while (e != null) {
            if (e instanceof HibernateException ||
                    (e.getMessage() != null && e.getMessage().contains("Unable to determine Dialect"))) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }
}
