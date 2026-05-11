package es.um.pds.tarjetas.domain.ports.input;

public interface ServicioAutenticacion {

    void enviarCodigoLogin(String email);

    String verificarCodigoLogin(String email, String codigo);
}