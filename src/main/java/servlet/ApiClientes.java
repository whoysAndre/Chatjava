
package servlet;

import dao.ClienteJpaController;
import dto.Cliente;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.JwtUtil;

@WebServlet(name = "ApiClientes", urlPatterns = {"/clientesv1"})
public class ApiClientes extends HttpServlet {

    ClienteJpaController clieDAO = new ClienteJpaController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        // Extraer el token del encabezado Bearer
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Quitar "Bearer " del inicio
        }
        List<Cliente> clientes = clieDAO.findClienteEntities();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        if (JwtUtil.validarToken(token)) {
            for (Cliente c : clientes) {
                JsonObjectBuilder obj = Json.createObjectBuilder()
                        .add("codCli", c.getCodCli())
                        .add("dniCli", c.getDniCli())
                        .add("apaCli", c.getApaCli())
                        .add("amaCli", c.getAmaCli())
                        .add("nomCli", c.getNomCli())
                        .add("fchNacCli", c.getFchNacCli().toString())
                        .add("logiCli", c.getLogiCli())
                        .add("pasCli", c.getPasCli());
                jsonArrayBuilder.add(obj);
            }
            String data = "{\"data\":" + jsonArrayBuilder.build().toString() + "}";
            out.print(data);
            out.flush();
        } else {
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("message", "token invalido")
                    .build();
            out.print(jsonResponse.toString());
            out.flush();
        }

    }
}
