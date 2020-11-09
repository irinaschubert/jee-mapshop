package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import de.java2enterprise.onlineshop.ejb.CustomerBeanLocal;
import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class BuyController implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction ut;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private CustomerBeanLocal customerBeanLocal;

    public String buyItem(Long id) {
    	Status statusSold;
        FacesContext ctx = FacesContext.getCurrentInstance();
        ELContext elc = ctx.getELContext();
        ELResolver elr = ctx.getApplication().getELResolver();
        SigninController signinController = (SigninController) elr.getValue(elc, null, "signinController");
        Customer customer = signinController.getCustomer();
        
        try {
        	statusSold = statusBeanLocal.findStatus(3L);
            ut.begin();
            Item item = em.find(Item.class, id);
            item.setBuyer(customer);
            item.setSold(LocalDateTime.now());
            item.setSeller(null); // in order to be able to delete seller customer seller has to be null
            item.setStatus(statusSold);
            ut.commit();
            FacesMessage m = new FacesMessage(
                    "Item successfully bought!",
                    item + " bought by " + customer.getEmail());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("cartForm", m);
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("cartForm", m);
        }
        return "/search.jsf";
    }
    
    public String buyItems(SigninController signinController) {
    	Status statusSold;
    	Status statusReserved;
    	Customer customer = signinController.getCustomer();
        customer = customerBeanLocal.findCustomer(customer.getId());
        statusSold = statusBeanLocal.findStatus(3L);
        statusReserved = statusBeanLocal.findStatus(4L);
    	
    	try {
	    	TypedQuery<Item> query = em.createQuery(
	                "FROM " + Item.class.getSimpleName() + " i "
	                        + "WHERE i.status= :status "
	                        + "AND i.buyer= :buyer",
	                Item.class);
	        query.setParameter("status", statusReserved);
	        query.setParameter("buyer", customer);
	        List<Item> reservedItems = query.getResultList();
	        if(reservedItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                    "No reserved Items found!",
	                    "No items found!");
	            FacesContext
	                    .getCurrentInstance()
	                    .addMessage("cartForm", m);
	        } else {
	        	for(int i = 0; i < reservedItems.size(); i++) {
	        		Item item = reservedItems.get(i);
	        		ut.begin();
	        		item.setStatus(statusSold);
	        		item.setSold(LocalDateTime.now());
	        		//item.setSeller(null); // in order to be able to delete seller customer seller has to be null
	        		item = em.merge(item);
	        		ut.commit();
	        	}
	            FacesMessage m = new FacesMessage(
	                    "Successfully bought items!",
	                    "Successfully bought reserved items");
	            FacesContext
	                    .getCurrentInstance()
	                    .addMessage("cartForm", m);
	        }
    } catch (Exception e) {
        FacesMessage m = new FacesMessage(
                FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
        FacesContext
                .getCurrentInstance()
                .addMessage("cartForm", m);
    }
    return "/cart.jsf";
    	
    }
}
