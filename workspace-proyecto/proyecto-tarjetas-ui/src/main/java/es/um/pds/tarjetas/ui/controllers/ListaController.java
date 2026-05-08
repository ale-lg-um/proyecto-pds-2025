package es.um.pds.tarjetas.ui.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.application.dto.ContenidoTarjetaCmd;
import es.um.pds.tarjetas.application.dto.ListaDTO;
import es.um.pds.tarjetas.application.dto.TarjetaDTO;
import es.um.pds.tarjetas.application.dto.TipoContenidoTarjeta;
import es.um.pds.tarjetas.ui.infrastructure.api.ListaApiClient;
import es.um.pds.tarjetas.ui.infrastructure.api.TarjetaApiClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

@Controller
@Scope("prototype")
public class ListaController {
	// Atributos
	private final ListaApiClient listaApi;
	private final TarjetaApiClient tarjetaApi;
	private final ApplicationContext contextoApp;
	private final ContextoUsuario contextoUsuario;
	private final SceneManager sceneManager;
	private ListaDTO listaDominio;		// Entidad real
	private String tableroId;
	private Runnable funcionEliminarDeLaVista;
	private BiConsumer<String, VBox> onTarjetaCreada;
	
	@FXML private Label lblNombreLista;
	@FXML private Label lblLimite;
	//@FXML private Label lblPrerrequisitos;
	@FXML private VBox contenedorTarjetas;
	@FXML private Button btnAnadirTarjeta;
	@FXML private Button btnRenombrarLista;
	
	// Aquí se inyecta el servicio
	public ListaController(ListaApiClient listaApi, TarjetaApiClient tarjetaApi, ApplicationContext contextoApp, ContextoUsuario contextoUsuario, SceneManager sceneManager) {
		this.listaApi = listaApi;
		this.tarjetaApi = tarjetaApi;
		this.contextoApp = contextoApp;
		this.contextoUsuario = contextoUsuario;
		this.sceneManager = sceneManager;
	}
	
	public void setTableroBloqueado(boolean bloqueado) {
		if(bloqueado) {
			btnAnadirTarjeta.setDisable(true);
			btnAnadirTarjeta.setText("Bloqueado");
			btnAnadirTarjeta.setStyle("-fx-opacity: 0.6; -fx-background-color: #cccccc;");
			
			btnRenombrarLista.setDisable(true);
			btnRenombrarLista.setText("Bloqueado");
			btnRenombrarLista.setStyle("-fx-opacity: 0.6; -fx-background-color: #cccccc;");
		} else {
			btnAnadirTarjeta.setDisable(false);
			btnAnadirTarjeta.setText("+ Añadir Tarjeta");
			btnAnadirTarjeta.setStyle("");
			
			btnRenombrarLista.setDisable(false);
			btnRenombrarLista.setText("Renombrar...");
			btnRenombrarLista.setStyle("");
		}
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
	        
	        List<TarjetaDTO> tarjetas = tarjetaApi.obtenerPorLista(listaId, contextoUsuario.getTokenSesion());
	        System.out.println("✅ Tarjetas encontradas: " + tarjetas.size());
	        
	        if (tarjetas.isEmpty()) {
	            System.out.println("⚠️  No hay tarjetas para esta lista");
	        }
	        
	        for(TarjetaDTO tarjeta : tarjetas) {
	            System.out.println("   - Creando visual para tarjeta: " + tarjeta.titulo());
	            instanciarTarjetaVisual(tarjeta);
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
				TarjetaDTO nuevaTarjeta = tarjetaApi.crearTarjeta(tableroId, listaDominio.id(), titulo, contenido, contextoUsuario.getTokenSesion());
				
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
				listaApi.eliminarLista(tableroId, listaDominio.id(), contextoUsuario.getTokenSesion());
				
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
	
	@FXML
	public void accionAjustarLimite(ActionEvent evento) {
		String valorActual = (listaDominio.limite() != null) ? listaDominio.limite().toString() : "";
		
		TextInputDialog dialogo = new TextInputDialog(valorActual);
		dialogo.setTitle("Límite");
		dialogo.setHeaderText("Configurar límite de tarjetas para la lista: " + listaDominio.nombre());
		dialogo.setContentText("Tarjetas máximas (nulo o 0 para sin límite):");
		
		dialogo.showAndWait().ifPresent(valor -> {
			try {
				Integer nuevoLimite = null;
				if(!valor.isBlank()) {
					int numero = Integer.parseInt(valor.trim());
					if(numero > 0) {
						nuevoLimite = numero;
					}
				}
				
				listaApi.configurarLimiteLista(tableroId, listaDominio.id(), nuevoLimite, contextoUsuario.getTokenSesion());
				
				sceneManager.showTablero();
			} catch (NumberFormatException e) {
				mostrarError("Error de formato", "Por favor, introduce un número válido.");
			} catch (IllegalStateException e) {
				// Este catch atrapará la excepción de tu dominio si intentas poner un límite de 2 en una lista que ya tiene 5
				mostrarError("No se puede aplicar", e.getMessage());
			} catch (Exception e) {
				mostrarError("Error", "No se pudo configurar el límite: " + e.getMessage());
			}
		});
	}
	
	@FXML
	public void accionConfigurarPrerrequisitos(ActionEvent evento) {
		try {			
			List<ListaDTO> listas = listaApi.obtenerListas(tableroId, contextoUsuario.getTokenSesion());
			
			Dialog<Set<String>> dialogo = new Dialog<>();
			dialogo.setTitle("Configurar prerrequisitos");
			dialogo.setHeaderText("Selecciona las listas por las que deben pasar antes las tarjetas");
			
			ButtonType btnAceptar = new ButtonType("Aceptar", ButtonData.OK_DONE);
			dialogo.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);
			
			VBox contenedor = new VBox();
			contenedor.setSpacing(10);
			contenedor.setPadding(new Insets(15));
			
			List<CheckBox> checks = new ArrayList<>();
			for(ListaDTO lista : listas) {
				CheckBox box = new CheckBox(lista.nombre());
				box.setUserData(lista.id());
				box.setSelected(listaDominio.prerrequisitoIds().contains(lista.id()));
				checks.add(box);
				contenedor.getChildren().add(box);
			}
			
			ScrollPane scroll = new ScrollPane(contenedor);
			dialogo.getDialogPane().setContent(scroll);
			
			dialogo.setResultConverter(button -> {
				if(button == btnAceptar) {
					return checks.stream()
							.filter(cb -> cb.isSelected())
							.map(cb -> (String) cb.getUserData())
							.collect(Collectors.toSet());
				}
				
				return null;
			});
			
			dialogo.showAndWait().ifPresent(prerreq -> {
				try {
					listaApi.configurarPrerrequisitosLista(tableroId, listaDominio.id(), prerreq, contextoUsuario.getTokenSesion());
					sceneManager.showTablero();
				} catch(Exception e) {
					mostrarError("Error", "No se pudieron configurar los prerrequisitos.");
				}
			});
		} catch(Exception e) {
			mostrarError("Error", "Error al abrir el diálogo: " + e.getMessage());
		}
	}
	
	@FXML
	public void accionDragOver(DragEvent evento) {
		if(evento.getDragboard().hasString()) {
			evento.acceptTransferModes(TransferMode.MOVE);
			contenedorTarjetas.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2;");
		}
		evento.consume();
	}
	
	@FXML
	public void accionDragDropped(DragEvent evento) {
		Dragboard db = evento.getDragboard();
		boolean exito = false;
		
		if(db.hasString()) {
			String[] datos = db.getString().split("\\|");
			String tarjetaId = datos[0];
			String listaOrigenId = datos[1];
			
			if(listaOrigenId.equals(listaDominio.id())) {
				mostrarError("Error", "La tarjeta ya está en esta lista");
				evento.setDropCompleted(exito);
				evento.consume();
				return;
			}
			
			try {
				System.out.println("Intentando mover tarjeta: " + tarjetaId + " a lista: " + listaDominio.id());
				
				TarjetaDTO tarjeta = tarjetaApi.obtenerTarjeta(tarjetaId, contextoUsuario.getTokenSesion());
				
				ListaDTO dest = listaApi.obtenerListaPorId(listaDominio.id(), contextoUsuario.getTokenSesion());
				
				System.out.println("Tarjeta encontrada: " + tarjeta.titulo());
				System.out.println("Lista encontrada: " + dest.nombre());
				
				if(!dest.prerrequisitoIds().isEmpty()) {
					System.out.println("📋 Validando prerequisitos: " + dest.prerrequisitoIds().size());
					
					for(String prerrequisito : dest.prerrequisitoIds()) {
						if(!tarjeta.listasVisitadas().contains(prerrequisito)) {
							ListaDTO listaDto = listaApi.obtenerListaPorId(prerrequisito, contextoUsuario.getTokenSesion());
									
							String nombreListaPrerrequisito = (listaDto != null) ? listaDto.nombre() : prerrequisito;
							
							mostrarError("Prerrequisitos incumplidos", "La tarjeta debe pasar por la lista '" + nombreListaPrerrequisito + "' antes de poder moverla a '" + dest.nombre() + "'");
							System.out.println("Prerrequisito no cumplido: " + nombreListaPrerrequisito);
							evento.setDropCompleted(exito);
							evento.consume();
							return;
						}
					}
				}
				
				System.out.println("Todos los prerrequisitos se han cumplido. Moviendo tarjeta...");
				tarjetaApi.moverTarjeta(tableroId, listaOrigenId, listaDominio.id(), tarjetaId, contextoUsuario.getTokenSesion());
				exito = true;
				System.out.println("Tarjeta movida con éxito");
				sceneManager.showTablero();
			} catch(Exception e) {
				e.printStackTrace();
				mostrarError("Error", "No se pudo mover la tarjeta: " + e.getMessage());
				exito = false;
			}
		}
		
		evento.setDropCompleted(exito);
		evento.consume();
	}
	
	@FXML
	public void accionRenombrarLista(ActionEvent evento) {
		TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Renombrar lista...");
		dialogo.setHeaderText("Renombrar la lista");
		dialogo.setContentText("Nuevo nombre:");
		dialogo.showAndWait().ifPresent(nombre -> {
			try {
				listaApi.renombrarLista(tableroId, listaDominio.id(), nombre, contextoUsuario.getTokenSesion());
				lblNombreLista.setText(nombre);
				sceneManager.showTablero();
			} catch(Exception e) {
				mostrarError("Error", "No se ha podido renombrar la lista.");
				e.printStackTrace();
			}
		});
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
