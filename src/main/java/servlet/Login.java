/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import dao.ClienteJpaController;
import dto.Cliente;
import java.io.IOException;
import java.io.PrintWriter;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.BcryptJava;
import utils.JwtUtil;


@WebServlet(name = "Login", urlPatterns = { "/login" })
public class Login extends HttpServlet {

  ClienteJpaController clienteDAO = new ClienteJpaController();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    PrintWriter out = response.getWriter();
    JsonReader jsonReader = Json.createReader(request.getReader());
    javax.json.JsonObject jsonObject = jsonReader.readObject();
    jsonReader.close();

    String username = jsonObject.getString("username");
    String password = jsonObject.getString("password");
    // 1. Buscar el cliente por username
    Cliente cliente = clienteDAO.findClienteByUsername(username);
    // 2. Verificar si el cliente existe y la contraseña coincide
    if (cliente == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    if (!BcryptJava.checkPassword(password, cliente.getPasCli())) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    JsonObject jsonResponse;
    String token = JwtUtil.generarToken(username);

    jsonResponse = Json.createObjectBuilder()
        .add("success", true)
        .add("token", token)
        .build();
    out.print(jsonResponse.toString());
    out.flush();
  }
}
