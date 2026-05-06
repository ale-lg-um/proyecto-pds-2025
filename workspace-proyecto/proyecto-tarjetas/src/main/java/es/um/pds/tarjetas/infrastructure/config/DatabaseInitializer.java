package es.um.pds.tarjetas.infrastructure.config;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {
	@Override
	public void run(String... args) throws Exception {
		try {
			String workingDir = System.getProperty("user.dir");
			String dbPath = workingDir + "\\datos_tarjetas.mv.db";
			
			System.out.println("Directorio de trabajo: " + workingDir);
			System.out.println("Base de datos guardada en: " + dbPath);
			
			if(Files.exists(Paths.get(dbPath))) {
				System.out.println("Base de datos encontrada");
			} else {
				System.out.println("Base de datos será creada en la primera ejecución");
			}
		} catch (Exception e) {
			System.err.println("Error al verificar base de datos" + e.getMessage());
		}
	}
}
