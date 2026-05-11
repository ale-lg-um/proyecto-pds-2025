package es.um.pds.tarjetas;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "es.um.pds.tarjetas", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquitecturaHexagonalArchUnitTest {

    @ArchTest
    static final ArchRule ninguna_interfaz_acaba_en_impl = noClasses().that().areInterfaces().should()
            .haveSimpleNameEndingWith("Impl")
            .because("Las interfaces no deben acabar por Impl");

    @ArchTest
    static final ArchRule controladores_rest_solo_en_adapters_rest = classes().that()
            .areAnnotatedWith(RestController.class)
            .or().areAnnotatedWith(Controller.class)
            .should().resideInAPackage("es.um.pds.tarjetas.adapters.rest..")
            .because("La entrada HTTP debe vivir en el adaptador REST");

    @ArchTest
    static final ArchRule dto_y_comandos_en_puertos_de_entrada = classes().that()
            .haveSimpleNameEndingWith("DTO")
            .or().haveSimpleNameEndingWith("Cmd")
            .should().resideInAPackage("es.um.pds.tarjetas.domain.ports.input..")
            .because("DTO y comandos forman parte del contrato de entrada de la aplicación");

    @ArchTest
    static final ArchRule repositorios_jpa_en_su_paquete = classes().that()
            .haveSimpleNameEndingWith("RepositoryJPA")
            .should().resideInAPackage("es.um.pds.tarjetas.adapters.jpa.repository..")
            .because("Los repositorios Spring Data JPA deben quedar agrupados en el adaptador JPA");

    @ArchTest
    static final ArchRule codigo_respeta_arquitectura_hexagonal = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy("es.um.pds.tarjetas.domain..", "es.um.pds.tarjetas.common.events..")
            .layer("Application").definedBy("es.um.pds.tarjetas.application..")
            .layer("Adapters").definedBy("es.um.pds.tarjetas.adapters..")
            .optionalLayer("Infrastructure").definedBy("es.um.pds.tarjetas.infrastructure..")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapters", "Infrastructure")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapters", "Infrastructure")
            .whereLayer("Adapters").mayNotBeAccessedByAnyLayer()
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();
}
