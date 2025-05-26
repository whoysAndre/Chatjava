/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ClienteJpaController;
import dto.Cliente;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import utils.JwtUtil;

/**
 *
 * @author yello
 */
@WebServlet(name = "GenerateReport", urlPatterns = { "/clientereporte" })
public class GenerateReport extends HttpServlet {

    ClienteJpaController cliDAO = new ClienteJpaController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        InputStream reportStream = null;

        // Verificar si el usuario está autenticado
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Quitar "Bearer " del inicio
        }
        
        if(!JwtUtil.validarToken(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            out.print("{\"message\":\"Token inválido\"}");
            out.flush();
            return;
        }

        try {

            // 1. Traer los datos de la base de datos
            List<Cliente> clientes = cliDAO.findClienteEntities();

            if (clientes == null || clientes.isEmpty()) {
                System.err.println("No se encontraron usuarios en la base de datos.");
                throw new IOException("No se encontraron usuarios en la base de datos.");
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(clientes);

            // 2. Cargar el .jrxml
            String reportPath = "/reports/reporte.jrxml";
            reportStream = getServletContext().getResourceAsStream(reportPath);

            if (reportStream == null) {
                System.err.println("No se pudo encontrar el archivo en: " + reportPath);
                throw new IOException("No se pudo encontrar el archivo reportUsuarios.jrxml en " + reportPath);
            }

            // 3. Compilar el reporte
            JasperReport report = JasperCompileManager.compileReport(reportStream);

            // 4. Llenar el reporte con tu propio datasource
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, null, dataSource);

            // 5. Enviar como PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=reportUsuarios.pdf");

            OutputStream out = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
