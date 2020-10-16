package de.java2enterprise.onlineshop;

import java.io.IOException;
import java.io.PrintWriter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test")
public class TestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @PersistenceUnit
    private EntityManagerFactory emf;
    
    public void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
    	
    	EntityManager em = emf.createEntityManager();
    	
        response.setContentType("text/html;charset=UTF-8");
        final PrintWriter writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html><body>");
        writer.println("Connected:" + em.isOpen());
        writer.println("</html></body>");
        em.close();
    }
}
