package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.time.LocalDateTime;
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
                    "Succesfully signed in!",
                    "You signed in under id " + customer.getId());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("registerForm", m);
    	}catch(Exception e) {
    		FacesMessage fm = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage(
                            "registerForm",
                            fm);
    	}
        return "register";
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
	                    "No Items found belonging to this customer!",
	                    "No items found!");
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
	                    "You deleted your account and the active items belonging to it.");
	            FacesContext
	                    .getCurrentInstance()
	                    .addMessage("searchForm", m);
	        }
	        
	        registerBeanLocal.removeCustomer(customer);
	        //TODO show deregistered page
	        }catch(Exception e) {
    		FacesMessage fm = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage(
                            "searchForm",
                            fm);
    	}
        return "deregister";
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
