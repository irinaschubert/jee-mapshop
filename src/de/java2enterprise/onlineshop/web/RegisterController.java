package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import org.eclipse.persistence.exceptions.DatabaseException;

import de.java2enterprise.onlineshop.ejb.RegisterBeanLocal;
import de.java2enterprise.onlineshop.ejb.SellBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class RegisterController implements Serializable {    
    private static final long serialVersionUID = 1L;
    
    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction ut;
    
    @Inject
    private Customer customer;
    
    private Status status1; //active
    private Status status4; //reserved
    
    @EJB
    private RegisterBeanLocal registerBeanLocal;
    @EJB
    private SellBeanLocal sellBeanLocal;

    public String persist() {
    	try {
    		registerBeanLocal.persist(customer);
    		FacesMessage m = new FacesMessage(
                "Succesfully registered new user!",
                "User " + customer.getEmail() + " has registered with ID " + customer.getId() + ".");
            FacesContext
                .getCurrentInstance()
                .addMessage("registerForm", m);
    	}
    	catch(Exception e) {
    		//TODO catch SQLIntegrityConstraintViolationException, doesn't work yet
    		if (e instanceof DatabaseException) {
        		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "A user with this e-mail already exists!",
                    "A user with e-mail " + customer.getEmail() + " already exists. Please choose another one.");
                FacesContext
                    .getCurrentInstance()
                    .addMessage("registerForm", m);
        	}
    		FacesMessage m = new FacesMessage(
    			FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
            FacesContext
                .getCurrentInstance()
                .addMessage("registerForm", m);
    	}
        return "/register.jsf";
    }

    public String removeCustomer(SigninController signinController) {
    	try {
    		Customer customer = signinController.getCustomer();
            customer = sellBeanLocal.findCustomer(customer.getId());
            status1 = sellBeanLocal.findStatus(1L); //active
            status4 = sellBeanLocal.findStatus(4L); //reserved
    		TypedQuery<Item> query = em.createQuery(
	                "FROM " + Item.class.getSimpleName() + " i "
	                        + "WHERE (i.status= :status1 "
	                		+ "OR i.status= :status4) "
	                        + "AND i.seller= :seller",
	                Item.class);
	        query.setParameter("seller", customer);
	        query.setParameter("status1", status1); //active
	        query.setParameter("status4", status4); //reserved
	        List<Item> activeItems = query.getResultList();
	        if(activeItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                    "No items found!",
	                    "No Items found belonging to customer " + customer.getEmail());
	            FacesContext
	                    .getCurrentInstance()
	                    .addMessage("accountForm", m);
	        } else {
	        	for(int i = 0; i < activeItems.size(); i++) {
	        		Item item = activeItems.get(i);
	        		ut.begin();
	        		if (!em.contains(item)) {
	            		item = em.merge(item);
	            	}
	        		em.remove(item);
	        		ut.commit();
	        	}
	        	
	    		FacesMessage m = new FacesMessage(
	                    "Succesfully deleted account!",
	                    "User account was successfully deleted including the active items belonging to it.");
	            FacesContext
	                    .getCurrentInstance()
	                    .addMessage("welcomeForm", m);
	        }
	        //TODO show deregister message
	        return registerBeanLocal.removeCustomer(customer);
	        
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("welcomeForm", m);
            //TODO show deregister message
            return "failCustomerRemove";
    	}
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
