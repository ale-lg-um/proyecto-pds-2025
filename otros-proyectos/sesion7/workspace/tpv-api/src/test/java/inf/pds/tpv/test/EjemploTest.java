package inf.pds.tpv.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EjemploTest {

    @Test
    void testAserciones() {

        // 1. Comprobación de igualdad (esperado, actual)
        assertEquals(2, 1 + 1, "Mensaje opcional de error");
        assertNotEquals(3, 1 + 1);

        // 2. Comprobación de condición
        assertTrue(5 > 3);
        assertFalse(5 < 3);

        // 3. Comprobación de nulo
        Object object = null;
        assertNull(object);
        assertNotNull("Hola");

        // 4. Comprobación de referencia (mismo objeto)
        String s1 = "test";
        String s2 = s1;
        assertSame(s1, s2);
        assertNotSame(new String("test"), new String("test"));

        // 5. Prueba de excepciones (valida el tipo de excepcion)
        // Se utiliza para comprobar que un método valida precondiciones correctamente
        assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        });

        // 6. Aserciones agrupadas (todas deben pasar)
        assertAll("Persona",
            () -> assertEquals("John", "John"),
            () -> assertEquals("Doe", "Doe")
        );
    }
}