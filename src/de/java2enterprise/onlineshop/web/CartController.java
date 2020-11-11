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
public class CartController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;
    
    public String reserveItem(Long id, SigninSignoutController signinSignoutController) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    	String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	Status statusInactive = statusBeanLocal.findStatus(2L);
    	Status statusReserved = statusBeanLocal.findStatus(4L);
        Customer customer = signinSignoutController.getCustomer();
        try {
            Item item = itemBeanLocal.findItem(id);
            Item newItem = new Item();
            newItem.setProductId(item.getProductId());
    		newItem.setTitle(item.getTitle());
    		newItem.setDescription(item.getDescription());
    		newItem.setFoto(item.getFoto());
    		newItem.setPrice(item.getPrice());
    		newItem.setSeller(item.getSeller());
    		newItem.setBuyer(customer);
    		newItem.setStockNumber(1L);
    		newItem.setStatus(statusReserved);
    		item.setStockNumber(item.getStockNumber()-1);
    		if(item.getStockNumber() == 0) {
    			item.setStatus(statusInactive);
    		}
    		itemBeanLocal.editItem(item);
    		itemBeanLocal.persistItem(newItem);
    		
    		String successDetail = ResourceBundle.getBundle("messages",locale).getString("successReserveItemDetail");
            FacesMessage m = new FacesMessage(
                    success,
                    successDetail);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("searchForm", m);
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("searchForm", m);
        }
        return "search.jsf";
    }
    
    public List<Item> findReservedItems(SigninSignoutController signinController) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	Status statusReserved = statusBeanLocal.findStatus(4L);
    	Customer buyer = signinController.getCustomer();
    	List<Item> reservedItems = new ArrayList<Item>();
    	
    	try {
    		reservedItems = itemBeanLocal.findItemsByStatusAndBuyer(statusReserved, buyer);
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("cartForm", m);
        }
        return reservedItems;
    }
}
