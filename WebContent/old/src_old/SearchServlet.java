package de.java2enterprise.onlineshop;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import de.java2enterprise.onlineshop.model.Item;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public final static int MAX_IMAGE_LENGTH = 400;

    @PersistenceContext
    private EntityManager em;
    
    @Resource
    private UserTransaction ut;

    protected void doPost(
            HttpServletRequest request, 
            HttpServletResponse response) 
                    throws ServletException, IOException {
    	
        response.setContentType("text/html;charset=UTF-8");
        
        String search = request.getParameter("search");
        try {        	
            TypedQuery<Item> query = 
                    em.createQuery(
                    "FROM " + 
                    Item.class.getSimpleName() + " i " +
                    "WHERE i.title like ?1 ",
                    Item.class);
            query.setParameter(1, search);
            List<Item> items = query.getResultList();
            
            for(Item item : items) {
            	System.out.println("item: " + item.toString());
            }
            
        } catch (Exception e) {
        	request.setAttribute("message", e.getMessage());
        }
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("search.jsp");
        dispatcher.forward(request, response);

    }
}
