package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
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
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class RemoveController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;

    public String deactivateItem(Long id) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	Status statusInactive = statusBeanLocal.findStatus(2L);
    	Status statusReserved = statusBeanLocal.findStatus(4L);
        try {
        	Item item = itemBeanLocal.findItem(id);
        	Long productId = item.getProductId();
            item.setStatus(statusInactive);
            item.setStockNumber(0L);
            itemBeanLocal.editItem(item);
            
            //remove only from buyer's cart not from history
            List<Item> childItems = itemBeanLocal.findChildItems(productId);
            for(int i = 0; i < childItems.size(); i++) {
            	if(childItems.get(i).getStatus().equals(statusReserved)) {
            		itemBeanLocal.removeItem(childItems.get(i));
            	}
            }
            
            String successDetail = ResourceBundle.getBundle("messages",locale).getString("successDeactivateItemDetail");
            FacesMessage m = new FacesMessage(
                    success,
                    successDetail);
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
        }
        return "account.jsf";
    }
    
    public String deleteItem(Long id) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	Status statusReserved = statusBeanLocal.findStatus(4L);
        try {
        	Item item = itemBeanLocal.findItem(id);
        	Long productId = item.getProductId();
            item.setSeller(null);
            item.setBuyer(null);
            itemBeanLocal.removeItem(item);
            
            //remove only from buyer's cart not from history
            List<Item> childItems = itemBeanLocal.findChildItems(productId);
            for(int i = 0; i < childItems.size(); i++) {
            	if(childItems.get(i).getStatus().equals(statusReserved)) {
            		itemBeanLocal.removeItem(childItems.get(i));
            	}
            }
            
            String successDetail = ResourceBundle.getBundle("messages",locale).getString("successDeleteItemDetail");
            FacesMessage m = new FacesMessage(
                    success,
                    successDetail);
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
        }
        return "account.jsf";
    }
    
    public String removeItemFromCart(Long id) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	Status statusActive = statusBeanLocal.findStatus(1L);
        try {
        	Item item = itemBeanLocal.findItem(id);
        	Item parentItem = itemBeanLocal.findItem(item.getProductId());
        	if(parentItem.getStockNumber() == 0) {
        		parentItem.setStatus(statusActive);
    		}
        	parentItem.setStockNumber(parentItem.getStockNumber()+1);
        	itemBeanLocal.editItem(parentItem);
        	
            item.setBuyer(null);
            item.setSeller(null);
            itemBeanLocal.removeItem(item);
        	
            String successDetail = ResourceBundle.getBundle("messages",locale).getString("successRemoveItemFromCartDetail");
            FacesMessage m = new FacesMessage(
                    success,
                    successDetail);
                FacesContext
                    .getCurrentInstance()
                    .addMessage("cartForm", m);
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
