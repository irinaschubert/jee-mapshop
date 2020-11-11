package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class BuyController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;
    
    public String buyItems(SigninSignoutController signinController) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    	String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String warning = ResourceBundle.getBundle("messages",locale).getString("warning");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	Status statusSold = statusBeanLocal.findStatus(3L);
    	Status statusReserved = statusBeanLocal.findStatus(4L);
    	Customer buyer = signinController.getCustomer();
    	
    	try {
    		List<Item> reservedItems = itemBeanLocal.findItemsByStatusAndBuyer(statusReserved, buyer);
	        if(reservedItems.isEmpty()) {
	        	String warnDetail = ResourceBundle.getBundle("messages",locale).getString("warnShowReservedItemsDetail");
	        	FacesMessage m = new FacesMessage(
	            		FacesMessage.SEVERITY_WARN,
	                    warning,
	                    warnDetail);
	            FacesContext
	                    .getCurrentInstance()
	                    .addMessage("cartForm", m);
	        } else {
	        	//update item: set status to sold and set sold date to now
	        	for(int i = 0; i < reservedItems.size(); i++) {
	        		Item item = reservedItems.get(i);
	        		item.setStatus(statusSold);
	        		item.setSold(LocalDateTime.now());
	        		itemBeanLocal.editItem(item);
	        	}
	        	String successDetail = ResourceBundle.getBundle("messages",locale).getString("successBuyItemsDetail");
	            FacesMessage m = new FacesMessage(
	                    success,
	                    successDetail);
	            FacesContext
	                    .getCurrentInstance()
	                    .addMessage("cartForm", m);
	        }
    } catch (Exception e) {
        FacesMessage m = new FacesMessage(
        		FacesMessage.SEVERITY_ERROR,
                error,
                tryAgain);
        FacesContext
                .getCurrentInstance()
                .addMessage("cartForm", m);
    }
    return "cart.jsf";
    	
    }
}
