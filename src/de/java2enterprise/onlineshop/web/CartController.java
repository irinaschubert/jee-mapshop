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
public class CartController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;
    
    public String reserveItem(Long id, SigninSignoutController signinSignoutController) {
    	Status statusInactive = statusBeanLocal.findStatus(2L);
    	Status statusReserved = statusBeanLocal.findStatus(4L);
        Customer customer = signinSignoutController.getCustomer();
        try {
            Item item = itemBeanLocal.findItem(id);
            Item newItem = new Item();
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
            FacesMessage m = new FacesMessage(
                    "Item reserved!",
                    item.getTitle() + " reserved by " + customer.getEmail());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("cartForm", m);
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("cartForm", m);
        }
        return "/search.jsf";
    }
    
    public List<Item> findReservedItems(SigninSignoutController signinController) {
    	Status statusReserved = statusBeanLocal.findStatus(4L);
    	Customer buyer = signinController.getCustomer();
    	List<Item> reservedItems = new ArrayList<Item>();
        
    	try {
    		reservedItems = itemBeanLocal.findItemsByStatusAndBuyer(statusReserved, buyer);
            if(reservedItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                        "No items in cart!",
                        "No items found!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("searchForm", m);
            } else {            	
                FacesMessage m = new FacesMessage(
                        "Success",
                        "Items successfully retrieved");
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
        return reservedItems;
    }
}
