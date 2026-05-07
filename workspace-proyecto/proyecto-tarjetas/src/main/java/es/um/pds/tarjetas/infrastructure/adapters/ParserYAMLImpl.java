package es.um.pds.tarjetas.infrastructure.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import es.um.pds.tarjetas.domain.model.plantilla.EspecificacionTableroPlantilla;
import es.um.pds.tarjetas.domain.model.plantilla.EspecificacionTableroPlantilla.EspecificacionListaPlantilla;
import es.um.pds.tarjetas.domain.model.plantilla.EspecificacionTableroPlantilla.EspecificacionTarjetaPlantilla;
import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;
import es.um.pds.tarjetas.domain.ports.output.PuertoParserYAML;

@Component
public class ParserYAMLImpl implements PuertoParserYAML {

    private final Yaml yamlParser = new Yaml();

    @Override
    public EspecificacionTableroPlantilla parse(String yaml) {
        try {
            if (yaml == null || yaml.isBlank()) {
                return null;
            }

            Object cargado = yamlParser.load(yaml);

            if (!(cargado instanceof Map<?, ?> rawRoot)) {
                throw new RuntimeException("El YAML debe tener una estructura de objeto");
            }

            Map<?, ?> root = rawRoot;

            /*
             * Aceptamos nombrePlantilla como formato principal.
             * También aceptamos nombre como alias por compatibilidad.
             */
            Object nombreObj = root.containsKey("nombrePlantilla")
                    ? root.get("nombrePlantilla")
                    : root.get("nombre");

            String nombrePlantilla = nombreObj != null ? nombreObj.toString() : null;

            Object listasObj = root.get("listas");

            List<EspecificacionListaPlantilla> listas = new ArrayList<>();

            if (listasObj instanceof List<?> rawListas) {
                for (Object objLista : rawListas) {
                    if (!(objLista instanceof Map<?, ?> rawLista)) {
                        throw new RuntimeException("Cada lista de la plantilla debe ser un objeto");
                    }

                    listas.add(mapLista(rawLista));
                }
            } else if (listasObj != null) {
                throw new RuntimeException("El campo 'listas' debe ser una lista");
            }

            return new EspecificacionTableroPlantilla(nombrePlantilla, listas);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("No se ha podido parsear el YAML de la plantilla", e);
        }
    }

    private EspecificacionListaPlantilla mapLista(Map<?, ?> raw) {
        String nombre = raw.get("nombre") != null
                ? raw.get("nombre").toString()
                : null;

        Integer limite = null;
        Object limiteObj = raw.get("limite");

        if (limiteObj instanceof Number number) {
            limite = number.intValue();
        } else if (limiteObj != null && !limiteObj.toString().equalsIgnoreCase("null")) {
            limite = Integer.parseInt(limiteObj.toString());
        }

        boolean especial = false;
        Object especialObj = raw.get("especial");

        if (especialObj instanceof Boolean bool) {
            especial = bool;
        } else if (especialObj != null) {
            especial = Boolean.parseBoolean(especialObj.toString());
        }

        List<String> prerrequisitos = new ArrayList<>();
        Object prerrequisitosObj = raw.get("prerrequisitos");

        if (prerrequisitosObj instanceof List<?> rawPrerrequisitos) {
            for (Object p : rawPrerrequisitos) {
                if (p != null) {
                    prerrequisitos.add(p.toString());
                }
            }
        } else if (prerrequisitosObj != null) {
            throw new RuntimeException("El campo 'prerrequisitos' debe ser una lista");
        }

        List<EspecificacionTarjetaPlantilla> tarjetas = new ArrayList<>();
        Object tarjetasObj = raw.get("tarjetas");

        if (tarjetasObj instanceof List<?> rawTarjetas) {
            for (Object objTarjeta : rawTarjetas) {
                if (!(objTarjeta instanceof Map<?, ?> rawTarjeta)) {
                    throw new RuntimeException("Cada tarjeta de la plantilla debe ser un objeto");
                }

                tarjetas.add(mapTarjeta(rawTarjeta));
            }
        } else if (tarjetasObj != null) {
            throw new RuntimeException("El campo 'tarjetas' debe ser una lista");
        }

        return new EspecificacionListaPlantilla(
                nombre,
                limite,
                prerrequisitos,
                especial,
                tarjetas
        );
    }

    private EspecificacionTarjetaPlantilla mapTarjeta(Map<?, ?> raw) {
        String titulo = raw.get("titulo") != null
                ? raw.get("titulo").toString()
                : null;

        /*
         * Aceptamos tipoContenido como formato principal.
         * También aceptamos tipo como alias.
         */
        Object tipoObj = raw.containsKey("tipoContenido")
                ? raw.get("tipoContenido")
                : raw.get("tipo");

        TipoContenidoTarjeta tipoContenido = parseTipoContenido(tipoObj);

        /*
         * Aceptamos descripcionTarea como formato principal.
         * También aceptamos descripcion como alias.
         */
        Object descripcionObj = raw.containsKey("descripcionTarea")
                ? raw.get("descripcionTarea")
                : raw.get("descripcion");

        String descripcionTarea = descripcionObj != null
                ? descripcionObj.toString()
                : null;

        List<String> itemsChecklist = new ArrayList<>();
        Object itemsObj = raw.get("itemsChecklist");

        if (itemsObj instanceof List<?> rawItems) {
            for (Object item : rawItems) {
                if (item != null) {
                    itemsChecklist.add(item.toString());
                }
            }
        } else if (itemsObj != null) {
            throw new RuntimeException("El campo 'itemsChecklist' debe ser una lista");
        }

        return new EspecificacionTarjetaPlantilla(
                titulo,
                tipoContenido,
                descripcionTarea,
                itemsChecklist
        );
    }

    private TipoContenidoTarjeta parseTipoContenido(Object tipoObj) {
        if (tipoObj == null) {
            return null;
        }

        String tipo = tipoObj.toString().trim().toUpperCase();

        try {
            return TipoContenidoTarjeta.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de contenido de tarjeta no válido: " + tipo);
        }
    }
}