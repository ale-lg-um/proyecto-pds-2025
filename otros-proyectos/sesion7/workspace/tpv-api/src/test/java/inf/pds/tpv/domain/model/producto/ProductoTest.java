package inf.pds.tpv.domain.model.producto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inf.pds.tpv.domain.model.producto.ProductoId.IdentificadorProductoException;

class ProductoTest {

	@Test
	void testProductoConDescuento10() throws IdentificadorProductoException {
		Producto p = new Producto(ProductoId.of(131L), "Berenjena", 3, 25);	// 1. Crear nuevo producto de prueba. La L de después del número indica que es un Long.
		int descuento = p.getDescuento();									// 2. Calcular su descuento
		
		assertEquals(10, descuento, 0);										// 3. Comparar valos esperado con valor obtenido (margen de error 0 porque estamos con enteros)
		//fail("No implementado");
	}
	
	@Test
	void testProductoConDescuento20() throws IdentificadorProductoException {
		Producto p = new Producto(ProductoId.of(132L), "Manzana Fuji", 1, 20);
		int descuento = p.getDescuento();
		
		assertEquals(20, descuento, 0);
	}

   @Test
	void testProductoSinDescuento() throws IdentificadorProductoException {
	   Producto p = new Producto(ProductoId.of(133L), "PlatanoCanarias", 5, 25);
	   int descuento = p.getDescuento();
		
		assertEquals(0, descuento, 0);
	   //fail("No implementado");
	}
	
	
}