package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
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
    	List<Item> offeredItems = new ArrayList<Item>();
    	Customer seller = signinController.getCustomer();
    	Status statusActive = statusBeanLocal.findStatus(1L);
        Status statusReserved = statusBeanLocal.findStatus(4L);
        
    	try {
    		offeredItems = itemBeanLocal.findItemsByStatusesAndSeller(statusActive, statusReserved, seller);
            if(offeredItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                    "No offered items found.",
                    "No offered items found for user " + seller.getEmail());
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
            } else {
                FacesMessage m = new FacesMessage(
                    "Offered items successfully retrieved.",
                    "Offered items successfully retrieved for user " + seller.getEmail());
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
            }
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
                FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        }
        return offeredItems;
    }
    
    public List<Item> findSoldItems(SigninSignoutController signinController) {
    	List<Item> soldItems = new ArrayList<Item>();
    	Customer seller = signinController.getCustomer();
        Status statusSold = statusBeanLocal.findStatus(3L);
        
    	try {
    		soldItems = itemBeanLocal.findItemsByStatusAndSeller(statusSold, seller);
            if(soldItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                	"No sold items found.",
                    "No sold items found for user " + seller.getEmail());
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
            } else {
                FacesMessage m = new FacesMessage(
                	"Sold items successfully retrieved.",
                    "Sold items successfully retrieved for user " + seller.getEmail());
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
            }
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
                FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        }
        return soldItems;
    }
    
    public List<Item> findBoughtItems(SigninSignoutController signinController) {
    	List<Item> boughtItems = new ArrayList<Item>();
        Status statusSold = statusBeanLocal.findStatus(3L);
        Customer buyer = signinController.getCustomer();
    	
    	try {
    		boughtItems = itemBeanLocal.findItemsByStatusAndBuyer(statusSold, buyer);
            if(boughtItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                    "No bought items found.",
                    "No bought items found for user " + buyer.getEmail());
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
            } else {
                FacesMessage m = new FacesMessage(
                	"Bought items successfully retrieved.",
                    "Bought items successfully retrieved for user " + buyer.getEmail());
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
            }
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
                FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        }
        return boughtItems;
    }
}
