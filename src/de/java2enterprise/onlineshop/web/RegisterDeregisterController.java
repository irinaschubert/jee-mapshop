package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	try {
    		customerBeanLocal.persistCustomer(customer);
    		String successDetail = ResourceBundle.getBundle("messages",locale).getString("successRegisterDetail");
            FacesMessage m = new FacesMessage(
                    success,
                    successDetail);
            FacesContext
                .getCurrentInstance()
                .addMessage("signinForm", m);
            return "signin.jsf";
    	}
    	catch(Exception e) {
    		//TODO catch SQLIntegrityConstraintViolationException, doesn't work yet
    		if (e instanceof DatabaseException) {
    			String errorDetail = ResourceBundle.getBundle("messages",locale).getString("errorRegisterDetail");
        		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    error,
                    errorDetail);
                FacesContext
                    .getCurrentInstance()
                    .addMessage("registerForm", m);
        	}
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                .getCurrentInstance()
                .addMessage("registerForm", m);
            return "register.jsf";
    	}
    }

    public String deregisterCustomer(SigninSignoutController signinController) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
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
            activeItems = itemBeanLocal.findItemsByTwoStatusesAndSeller(statusActive, statusReserved, customer);
	        if(!activeItems.isEmpty()) {
	        	for(int i = 0; i < activeItems.size(); i++) {
	        		Item item = activeItems.get(i);
	        		item.setSeller(null);
	        		itemBeanLocal.removeItem(item);
	        	}
	        }
	        
	        //find bought items to clean up
	        boughtItems = itemBeanLocal.findItemsByStatusAndBuyer(statusSold, customer);
	        if(!boughtItems.isEmpty()) {
	        	for(int i = 0; i < boughtItems.size(); i++) {
	        		Item item = boughtItems.get(i);
	        		item.setBuyer(null);
	        		itemBeanLocal.editItem(item);
	        	}
	        }
	        
	        //find reserved items to clean up
	        reservedItems = itemBeanLocal.findItemsByStatusAndBuyer(statusReserved, customer);
	        if(!reservedItems.isEmpty()) {
	        	for(int i = 0; i < reservedItems.size(); i++) {
	        		Item item = reservedItems.get(i);
	        		item.setBuyer(null);
	        		item.setStatus(statusActive);
	        		itemBeanLocal.editItem(item);
	        	}
	        }
	        
	        //find sold items to clean up
	        soldItems = itemBeanLocal.findItemsByStatusAndSeller(statusSold, customer);
	        if(!soldItems.isEmpty()) {
	        	for(int i = 0; i < soldItems.size(); i++) {
	        		Item item = soldItems.get(i);
	        		item.setSeller(null);
	        		itemBeanLocal.editItem(item);
	        	}
	        }
	        
	        //log out after deregistering
	        String result = customerBeanLocal.removeCustomer(customer);
	        if(result.equals("customerRemoved")) {
	        	this.setCustomer(null);
		    	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		    	String successDetail = ResourceBundle.getBundle("messages",locale).getString("successDeregisterDeatil");
	            FacesMessage m = new FacesMessage(
	                    success,
	                    successDetail);
	            FacesContext
	            	.getCurrentInstance()
	            	.addMessage("signinForm", m);
	        }
    	}catch(Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                .getCurrentInstance()
                .addMessage("signinForm", m);
    	}
    	return "signin.jsf";
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
