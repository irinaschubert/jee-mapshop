package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class BuyController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;
    
    public String buyItems(SigninController signinController) {
    	Status statusSold = statusBeanLocal.findStatus(3L);
    	Status statusReserved = statusBeanLocal.findStatus(4L);
    	Customer buyer = signinController.getCustomer();
    	
    	try {
    		List<Item> reservedItems = itemBeanLocal.findItemsByStatusAndBuyer(statusReserved, buyer);
	        if(reservedItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                    "No reserved Items found!",
	                    "No items found!");
	            FacesContext
	                    .getCurrentInstance()
	                    .addMessage("cartForm", m);
	        } else {
	        	//update items with status (sold) and sold date
	        	for(int i = 0; i < reservedItems.size(); i++) {
	        		Item item = reservedItems.get(i);
	        		item.setStatus(statusSold);
	        		item.setSold(LocalDateTime.now());
	        		itemBeanLocal.editItem(item);
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
