package de.java2enterprise.onlineshop.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/image")
public class ImageServlet extends HttpServlet {
	
    private static final long serialVersionUID = 1L;
    
    private final static Logger log = Logger.getLogger(ImageServlet.class.toString());

    @PersistenceContext
    private EntityManager em;

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String id = request.getParameter("id");
            Query query = em.createQuery(
                            "select i.foto "
                                    + "from Item i "
                                    + "where i.id = :id");
            query.setParameter("id", Long.parseLong(id));
            byte[] foto = (byte[]) query.getSingleResult();
            if(foto != null) {
            	response.reset();
                response.getOutputStream().write(foto);
            }
        } catch (Exception e) {
        	log.severe(e.getMessage());
            throw new ServletException(e.getMessage());
        }
    }
}
