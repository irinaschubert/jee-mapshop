package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import de.java2enterprise.onlineshop.ejb.SellBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class BuyController implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger log = Logger
            .getLogger(BuyController.class.toString());

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction ut;
    
    //@Inject
    private Status status3;
    private Status status4;
    
    @EJB
    private SellBeanLocal sellBeanLocal;

    public String buyItem(Long id) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ELContext elc = ctx.getELContext();
        ELResolver elr = ctx.getApplication().getELResolver();
        SigninController signinController = (SigninController) elr
                .getValue(
                        elc,
                        null,
                        "signinController");

        Customer customer = signinController.getCustomer();
        try {
        	status3 = sellBeanLocal.findStatus(3L); //sold
            ut.begin();
            Item item = em.find(Item.class, id);
            item.setBuyer(customer);
            item.setSold(LocalDateTime.now());
            item.setSeller(null); // in order to be able to delete seller customer seller has to be null
            item.setStatus(status3);
            ut.commit();
            log.info(item + " bought by " + customer.getEmail());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return "/search.jsf";
    }
    
    public String buyItems(SigninController signinController) {
    	Customer customer = signinController.getCustomer();
        customer = sellBeanLocal.findCustomer(customer.getId());
        status3 = sellBeanLocal.findStatus(3L); //sold
        status4 = sellBeanLocal.findStatus(4L); //reserved
    	
    	try {
	    	TypedQuery<Item> query = em.createQuery(
	                "SELECT i FROM Item i "
	                        + "WHERE i.status= :status "
	                        + "AND i.buyer= :buyer",
	                Item.class);
	        query.setParameter("status", status4);
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
	        		item.setStatus(status3);
	        		item.setSold(LocalDateTime.now());
	        		item.setSeller(null); // in order to be able to delete seller customer seller has to be null
	        		item = em.merge(item);
	        		ut.commit();
	        		log.info(item + " bought by " + customer.getEmail());
	        	}
	            FacesMessage m = new FacesMessage(
	                    "Successfully bought items!",
	                    "Successfully bought reserved items");
	            FacesContext
	                    .getCurrentInstance()
	                    .addMessage("cartForm", m);
	        }
    } catch (Exception e) {
        FacesMessage fm = new FacesMessage(
                FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
        FacesContext
                .getCurrentInstance()
                .addMessage(
                        "cartForm",
                        fm);
    }
    return "/cart.jsf";
    	
    }
}
