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
import javax.inject.Named;

import de.java2enterprise.onlineshop.ejb.ItemBeanLocal;
import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class AccountController implements Serializable {
	
    private static final long serialVersionUID = 1L;

    private List<Item> items;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;

    public List<Item> getItems() {
        this.items = itemBeanLocal.findAll();
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public List<Item> findOfferedItems(SigninSignoutController signinController) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	List<Item> offeredItems = new ArrayList<Item>();
    	Customer seller = signinController.getCustomer();
    	Status statusActive = statusBeanLocal.findStatus(1L);
    	Status statusInactive = statusBeanLocal.findStatus(2L);
        
    	try {
    		offeredItems = itemBeanLocal.findItemsByTwoStatusesAndSeller(statusActive, statusInactive, seller);
    		if(offeredItems.isEmpty()) {
            	
            } else {
            	
            }
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        }
        return offeredItems;
    }
    
    public List<Item> findSoldItems(SigninSignoutController signinController) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	List<Item> soldItems = new ArrayList<Item>();
    	Customer seller = signinController.getCustomer();
        Status statusSold = statusBeanLocal.findStatus(3L);
        
    	try {
    		soldItems = itemBeanLocal.findItemsByStatusAndSeller(statusSold, seller);
            if(soldItems.isEmpty()) {
            	
            } else {
            	
            }
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        }
        return soldItems;
    }
    
    public List<Item> findBoughtItems(SigninSignoutController signinController) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	List<Item> boughtItems = new ArrayList<Item>();
        Status statusSold = statusBeanLocal.findStatus(3L);
        Customer buyer = signinController.getCustomer();
    	
    	try {
    		boughtItems = itemBeanLocal.findItemsByStatusAndBuyer(statusSold, buyer);
            if(boughtItems.isEmpty()) {
            	
            } else {
            	
            }
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        }
        return boughtItems;
    }
}
