package idscm.rest.resources;


import daos.VideoDAO;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import models.Video;

@Path("/jakartaee91")
public class JakartaEE91Resource {

    private VideoDAO videoDAO = new VideoDAO();

    // ----------------------
    // Método de prueba: Ping
    // ----------------------
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public Response ping() {
        return Response.ok("Servicio REST activo").build();
    }

    // ------------------------------------------------------------
    // Método para actualizar el número de reproducciones de un vídeo
    // ------------------------------------------------------------
    /*
      Se espera que se reciba un JSON con la siguiente estructura:
      {
         "videoId": 123,
         "increment": 1
      }
    */
    @PUT
    @Path("/actualizarReproducciones")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarReproducciones(JsonObject jsonInput) throws SQLException {
         // Extraer los campos del JSON
        int videoId = jsonInput.getInt("videoId", -1);
        int incremento = jsonInput.getInt("increment", 0);
        if (videoId == -1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"videoId no proporcionado\"}")
                    .build();
        }
        
        boolean actualizado = videoDAO.actualizarReproducciones(videoId, incremento);
        if (actualizado) {
            return Response.ok("{\"estado\":\"actualizado\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"video no encontrado\"}")
                    .build();
        }
    }

    
    // ---------------------------------------------------------
    // Búsqueda de vídeos por título
    // ---------------------------------------------------------
    @GET
    @Path("/buscar/titulo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorTitulo(@QueryParam("titulo") String titulo) throws SQLException {
        List<Video> resultados = videoDAO.buscarPorTitulo(titulo);
        return Response.ok(resultados).build();
    }

    // --------------------------------------------
    // Búsqueda de vídeos por autor
    // --------------------------------------------
    @GET
    @Path("/buscar/autor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorAutor(@QueryParam("autor") String autor) throws SQLException {
        List<Video> resultados = videoDAO.buscarPorAutor(autor);
        return Response.ok(resultados).build();
    }

    
    // --------------------------------------------
    // Búsqueda de vídeos por fecha de creación
    // --------------------------------------------
    /*
      Se permite pasar la fecha en distintos formatos (por ejemplo: "yyyy", "MM/yyyy" o "dd/MM/yyyy").
      El parámetro se recibe como un String y se intenta parsear.
    */
    @GET
    @Path("/buscar/fecha")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorFecha(@QueryParam("fecha") String fechaStr) throws SQLException {
        List<Video> resultados = videoDAO.buscarPorFecha(fechaStr);
        return Response.ok(resultados).build();
    }
}




