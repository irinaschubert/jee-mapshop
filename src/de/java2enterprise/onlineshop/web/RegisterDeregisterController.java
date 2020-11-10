package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.persistence.exceptions.DatabaseException;

import de.java2enterprise.onlineshop.ejb.CustomerBeanLocal;
import de.java2enterprise.onlineshop.ejb.ItemBeanLocal;
import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class RegisterDeregisterController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @Inject
    private Customer customer;
    
    @EJB
    private CustomerBeanLocal customerBeanLocal;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;

    public String registerCustomer() {
    	try {
    		customerBeanLocal.persistCustomer(customer);
    		FacesMessage m = new FacesMessage(
                "Succesfully registered!",
                "User " + customer.getEmail() + " has registered with ID " + customer.getId() + ".");
            FacesContext
                .getCurrentInstance()
                .addMessage("signinForm", m);
            return "/signin.jsf";
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
            
            return "/register.jsf";
    	}
    }

    public String deregisterCustomer(SigninSignoutController signinController) {
    	Customer customer = signinController.getCustomer();
		Status statusActive = statusBeanLocal.findStatus(1L);
        Status statusSold = statusBeanLocal.findStatus(3L);
        Status statusReserved = statusBeanLocal.findStatus(4L);
        List<Item> activeItems = new ArrayList<Item>();
        List<Item> boughtItems = new ArrayList<Item>();
        List<Item> reservedItems = new ArrayList<Item>();
        List<Item> soldItems = new ArrayList<Item>();
    	try {
            //find active items to delete
            activeItems = itemBeanLocal.findItemsByStatusesAndSeller(statusActive, statusReserved, customer);
	        if(activeItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                "No sold and active items found!",
	                "No sold and active items found belonging to customer " + customer.getEmail());
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("signinForm", m);
	        } else {
	        	for(int i = 0; i < activeItems.size(); i++) {
	        		Item item = activeItems.get(i);
	        		item.setSeller(null);
	        		itemBeanLocal.removeItem(item);
	        	}
	    		FacesMessage m = new FacesMessage(
	                "Succesfully cleand up sold and active items of user!",
	                "Cleand up sold and active items of user by setting seller to null.");
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("signinForm", m);
	        }
	        
	        //find bought items to clean up
	        boughtItems = itemBeanLocal.findItemsByStatusAndBuyer(statusSold, customer);
	        if(boughtItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                "No bought items found!",
	                "No bought items found belonging to customer " + customer.getEmail());
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("signinForm", m);
	        } else {
	        	for(int i = 0; i < boughtItems.size(); i++) {
	        		Item item = boughtItems.get(i);
	        		item.setBuyer(null);
	        		itemBeanLocal.editItem(item);
	        	}
	    		FacesMessage m = new FacesMessage(
	                "Succesfully cleand up bought items of user!",
	                "Cleand up bought items of user by setting buyer to null.");
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("signinForm", m);
	        }
	        
	        //find reserved items to clean up
	        reservedItems = itemBeanLocal.findItemsByStatusAndBuyer(statusReserved, customer);
	        if(reservedItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                "No bought items found!",
	                "No bought items found belonging to customer " + customer.getEmail());
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("signinForm", m);
	        } else {
	        	for(int i = 0; i < reservedItems.size(); i++) {
	        		Item item = reservedItems.get(i);
	        		item.setBuyer(null);
	        		itemBeanLocal.editItem(item);
	        	}
	    		FacesMessage m = new FacesMessage(
	                "Succesfully cleand up reserved items of user!",
	                "Cleand up reserved items of user by setting buyer to null.");
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("signinForm", m);
	        }
	        
	        //find sold items to clean up
	        soldItems = itemBeanLocal.findItemsByStatusAndSeller(statusSold, customer);
	        if(soldItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                "No sold items found!",
	                "No sold items found belonging to customer " + customer.getEmail());
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("signinForm", m);
	        } else {
	        	for(int i = 0; i < soldItems.size(); i++) {
	        		Item item = soldItems.get(i);
	        		item.setSeller(null);
	        		itemBeanLocal.editItem(item);
	        	}
	    		FacesMessage m = new FacesMessage(
	                "Succesfully cleand up bought items of user!",
	                "Cleand up bought items of user by setting buyer to null.");
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("signinForm", m);
	        }
	        
	        //TODO show deregister message
	        String result = customerBeanLocal.removeCustomer(customer);
	        if(result == "customerRemoved") {
	        	this.setCustomer(null);
		    	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	        }
	        
	        return "signin";
	        
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
