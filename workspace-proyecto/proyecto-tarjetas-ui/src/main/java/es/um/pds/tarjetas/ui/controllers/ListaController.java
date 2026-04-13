package es.um.pds.tarjetas.ui.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ItemChecklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
//import es.um.pds.tarjetas.domain.ports.input.ServicioGestionTablero;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.ServicioTarjeta;
import es.um.pds.tarjetas.domain.ports.input.commands.ContenidoTarjetaCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TareaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

@Controller
@Scope("prototype")
public class ListaController {
	// Atributos
	private final ServicioTablero servicioTablero;
	private final ServicioTarjeta servicioTarjeta;
	private final ServicioLista servicioLista;
	private final ApplicationContext contextoApp;
	private final RepositorioTarjetas repoTarjetas;
	private final ContextoUsuario contextoUsuario;
	private ListaDTO listaDominio;		// Entidad real
	private String tableroId;
	private Runnable funcionEliminarDeLaVista;
	private BiConsumer<String, VBox> onTarjetaCreada;
	
	@FXML private Label lblNombreLista;
	@FXML private Label lblLimite;
	@FXML private VBox contenedorTarjetas;
	
	// Aquí se inyecta el servicio
	public ListaController(ServicioTablero servicioTablero, ServicioTarjeta servicioTarjeta, ServicioLista servicioLista, ApplicationContext contextoApp, RepositorioTarjetas repoTarjetas, ContextoUsuario contextoUsuario) {
		this.servicioTablero = servicioTablero;
		this.servicioTarjeta = servicioTarjeta;
		this.servicioLista = servicioLista;
		this.contextoApp = contextoApp;
		this.repoTarjetas = repoTarjetas;
		this.contextoUsuario = contextoUsuario;
	}
	
	public void setFuncionEliminarDeLaVista(Runnable funcion) {
		this.funcionEliminarDeLaVista = funcion;
	}
	
	public void setOnTarjetaCreada(BiConsumer<String, VBox> callback) {
		this.onTarjetaCreada = callback;
	}
	
	// El tablero llama a este método depsués de crear la lista
	public void configurarLista(ListaDTO lista, String tableroId) {
		this.listaDominio = lista;
		this.lblNombreLista.setText(lista.nombre());
		this.tableroId = tableroId;
		
		// Revisar si la lista tiene límite
		if(lista.limite() != null) {
			this.lblLimite.setText("(0/" + lista.limite() + ")");
		} else {
			this.lblLimite.setText("(\u221E)");
		}
		
		cargarTarjetasBD();
	}
	
	private void cargarTarjetasBD() {
	    try {
	        System.out.println("📦 Intentando cargar tarjetas para lista: " + this.listaDominio.id());
	        
	        String listaId = this.listaDominio.id();
	        System.out.println("   ListaId creado: " + listaId);
	        
	        List<Tarjeta> tarjetas = repoTarjetas.buscarPorListaId(ListaId.of(listaId));
	        System.out.println("✅ Tarjetas encontradas: " + tarjetas.size());
	        
	        if (tarjetas.isEmpty()) {
	            System.out.println("⚠️  No hay tarjetas para esta lista");
	        }
	        
	        for(Tarjeta tarjeta : tarjetas) {
	            System.out.println("   - Creando visual para tarjeta: " + tarjeta.getTitulo());
	            instanciarTarjetaVisual(new TarjetaDTO(tarjeta));
	        }
	        
	        System.out.println("✅ Se cargaron " + tarjetas.size() + " tarjetas en la UI");
	    } catch(Exception e) {
	        System.err.println("❌ Error al cargar tarjetas: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	@FXML
	public void accionAnadirTarjeta(ActionEvent evento) {
		System.out.println("Botón 'Añadir Tarjeta' pulsado en la lista: " + listaDominio.nombre());
		
		// Diálogo genérico
		Dialog<Pair<String, String>> dialogo = new Dialog<>();
		dialogo.setTitle("Nueva Tarjeta");
		dialogo.setHeaderText("Añadir nueva tarjeta a: " + listaDominio.nombre());
		
		// Botones de aceptar y cancelar
		ButtonType btnAceptar = new ButtonType("Aceptar", ButtonData.OK_DONE);
		ButtonType btnCancelar = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
		dialogo.getDialogPane().getButtonTypes().addAll(btnAceptar, btnCancelar);
		
		// Alinear textos e inputs
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		// Crear campo de texto y desplegable
		TextField nombre = new TextField();
		nombre.setPromptText("Titulo:");
		
		ComboBox<String> opciones = new ComboBox<>();
		opciones.getItems().addAll(TipoContenidoTarjeta.TAREA.toString(), TipoContenidoTarjeta.CHECKLIST.toString());
		opciones.getSelectionModel().selectFirst();
		
		// Añadir todo a la vista
		grid.add(new Label("Título:"), 0, 0);
		grid.add(nombre, 1, 0);
		grid.add(new Label("Tipo:"), 0, 1);
		grid.add(opciones, 1, 1);
		
		dialogo.getDialogPane().setContent(grid);
		
		dialogo.setResultConverter(dialogButton -> {
			if(dialogButton == btnAceptar) {
				return new Pair<>(nombre.getText(), opciones.getValue());
			}
			return null;
		});
		
		/*TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Nueva tarea");
		dialogo.setHeaderText("Añadir tarjeta a: " + listaDominio.nombre());
		dialogo.setContentText("Título:");*/
		
		dialogo.showAndWait().ifPresent(resultado -> {
			String titulo = resultado.getKey();
			String opcion = resultado.getValue();
			
			try {
				System.out.println("Creando tarjeta: " + titulo);
				
				//TarjetaDTO tarjetaDTO = new TarjetaDTO(null, titulo, null, listaDominio.getIdentificador().getId(), 0, new TareaDTO(""), List.of(), Set.of());
				
				// CAMBIAR: tenemos que detectar al usuario que está con la sesión iniciada
				//String usuario = "usuario@ejemplo.com";
				String usuario = contextoUsuario.getEmail();
				ContenidoTarjetaCmd contenido;
				if(TipoContenidoTarjeta.valueOf(opcion).equals(TipoContenidoTarjeta.TAREA)) {
					contenido = new ContenidoTarjetaCmd(TipoContenidoTarjeta.valueOf(opcion), titulo, List.of(), usuario);
				} else {
					contenido = inicializarChecklist(titulo, usuario);
				}
				
				//TarjetaDTO nuevaTarjeta = servicioTablero.crearTarjeta(tableroId, listaDominio.getIdentificador().getId(), tarjetaDTO, "usuario@ejemplo.com");
				TarjetaDTO nuevaTarjeta = servicioTarjeta.crearTarjeta(tableroId, listaDominio.id(), titulo, contenido);
				
				// cargar el FXML del post-it con Spring
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MiniTarjetaView.fxml"));
				loader.setControllerFactory(contextoApp::getBean);
				
				VBox nodoTarjeta = loader.load();
				
				// pasar datos a post-it
				MiniTarjetaController controlador = loader.getController();
				controlador.configurarMiniTarjeta(nuevaTarjeta);
				
				// Inyectar en la lista
				contenedorTarjetas.getChildren().add(nodoTarjeta);
				
				System.out.println("TARJETA AÑADIDA");
			} catch(Exception e) {
				e.printStackTrace();
				mostrarError("Error", "No se pudo crear la tarjeta: " +  e.getMessage());
			}
		});
	}
	
	@FXML
	public void accionEliminarLista(ActionEvent evento) {
		Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
		alerta.setTitle("Eliminar Lista");
		alerta.setHeaderText(null);
		alerta.setContentText("¿Seguro que quieres eliminar la lista '" + listaDominio.nombre() + "' y todas sus tarjetas?");
		
		if(alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
			try {
				servicioLista.eliminarLista(tableroId, listaDominio.id(), contextoUsuario.getEmail());
				
				if(funcionEliminarDeLaVista != null) {
					funcionEliminarDeLaVista.run();
				}
				
				System.out.println("Lista eliminada");
			} catch(Exception e) {
				e.printStackTrace();
				mostrarError("Error al eliminar", "No se pudo eliminar la lista: " + e.getMessage());
			}
		}
	}
	
	private void instanciarTarjetaVisual(TarjetaDTO tarjeta) {
	    try {
	        System.out.println("🎨 Instanciando tarjeta visual: " + tarjeta.titulo());
	        
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MiniTarjetaView.fxml"));
	        loader.setControllerFactory(contextoApp::getBean);
	        
	        VBox nodoTarjeta = loader.load();
	        System.out.println("   ✅ FXML cargado");
	        
	        //nodoTarjeta.setId("t-" + tarjeta.id());
	        
	        MiniTarjetaController controlador = loader.getController();
	        controlador.configurarMiniTarjeta(tarjeta);
	        System.out.println("   ✅ Controlador configurado");
	        
	        if(onTarjetaCreada != null) {
	        	onTarjetaCreada.accept(tarjeta.id(), nodoTarjeta);
	        }
	        
	        controlador.setFuncionEliminarDeLaVista(() -> {
	        	contenedorTarjetas.getChildren().remove(nodoTarjeta);
	        });
	        
	        contenedorTarjetas.getChildren().add(nodoTarjeta);
	        System.out.println("   ✅ Tarjeta añadida al contenedor");
	        
	    } catch(Exception e) {
	        System.err.println("❌ Error al instanciar tarjeta visual: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	// Añadir el primer elemento a una checklist (no pueden estar vacías)
	private ContenidoTarjetaCmd inicializarChecklist(String titulo, String usuario) throws Exception{
		// new ArrayList<>() en lugar de List.of() porque el of devuelve lista inmutable, por lo que no se puede añadir el primer item a la checklist
		ContenidoTarjetaCmd contenido = new ContenidoTarjetaCmd(TipoContenidoTarjeta.CHECKLIST, titulo, new ArrayList<>(), usuario);
		TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Nuevo ítem");
		dialogo.setHeaderText("Añadir primer ítem a la tarjeta");
		dialogo.setContentText("Ítem:");
		
		dialogo.showAndWait().ifPresent(item -> {
			try {
				ItemChecklist i = ItemChecklist.of(item);
				contenido.itemsChecklist().add(item);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
		return contenido;
	}
	
	private void mostrarError(String titulo, String mensaje) {
		Alert alerta = new Alert(AlertType.ERROR);
		alerta.setTitle(titulo);
		alerta.setHeaderText(null);
		alerta.setContentText(mensaje);
		alerta.showAndWait();
	}
}
