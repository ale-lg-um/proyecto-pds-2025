package es.um.pds.tarjetas.infrastructure.config;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {
	@Override
	public void run(String... args) throws Exception {
		String dbPath = "./datos:tarjetas.v.db";
		
		if(Files.exists(Paths.get(dbPath))) {
			System.out.println("Base de datos encontrada en: " + Paths.get(dbPath).toAbsolutePath());
		} else {
			System.out.println("Base de datos creándose en: " + Paths.get(dbPath).toAbsolutePath());
		}
		
		System.out.println("Directorio de trabajo: " + System.getProperty("user.dir"));
	}
}
