package de.java2enterprise.onlineshop.web;

import java.io.Serializable;

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
    	Status statusInactive = statusBeanLocal.findStatus(2L);
        try {
        	Item item = itemBeanLocal.findItem(id);
            item.setStatus(statusInactive);
            item.setStockNumber(0L);
            itemBeanLocal.editItem(item);
            FacesMessage m = new FacesMessage(
                "Succesfully deactivated item!",
                item.getTitle() + " deactivated.");
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
        }
        return "/account.jsf";
    }
    
    public String removeItemFromCart(Long id) {
    	Status statusActive = statusBeanLocal.findStatus(1L);
        try {
        	Item item = itemBeanLocal.findItem(id);
            item.setStatus(statusActive);
            item.setBuyer(null);
            itemBeanLocal.editItem(item);
        	
            FacesMessage m = new FacesMessage(
                    "Succesfully removed item from cart!",
                    item.getTitle() + " removed.");
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
        }
        return "/cart.jsf";
    }
}
