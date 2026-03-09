package inf.pds.tpv.test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "inf.pds.tpv")
class TpvApiApplicationTests {
	
	/*
	 * Regla para detectar que no haya System.out.println dentro del codigo (se excluye el paquete de los test)
	 * */
	@ArchTest
	static final ArchRule no_system_out = noClasses().that()
	.resideOutsideOfPackage("..test..").should()
			.accessField(System.class, "out");
	/* 
	 * Regla para detectar que no se dejen exception.printStackTrace()
	 */
	@ArchTest
	static final ArchRule no_print_stacktrace =
	    noClasses()
	        .should()
	        .callMethod(Throwable.class, "printStackTrace");
	

	/*
	 * En una arquitectura hexagonal los imports solo pueden ser de capas inferiores
	 * Si hacemos import de una capa superior estamos rompiendo la arquitectura
	 * 
	 */
	@ArchTest
	static final ArchRule check_hexagonal_architecture = layeredArchitecture()
	.consideringAllDependencies()
			.layer("Domain").definedBy("..domain..")
			.layer("Application").definedBy("..application..")
			.layer("Adapters")
			.definedBy("..adapters..")
			// Al dominio pueden acceder las dos capas superiores
			.whereLayer("Domain").mayOnlyBeAccessedByLayers("Adapters", "Application")
			.whereLayer("Application")
			.mayOnlyBeAccessedByLayers("Adapters")
			.whereLayer("Adapters").mayNotBeAccessedByAnyLayer();

	
}