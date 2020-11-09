package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.el.ELContext;
import javax.el.ELResolver;
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
    
    public String reserveItem(Long id) {
    	Status statusReserved = statusBeanLocal.findStatus(4L);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ELContext elc = ctx.getELContext();
        ELResolver elr = ctx.getApplication().getELResolver();
        SigninSignoutController signinController = (SigninSignoutController) elr
                .getValue(
                        elc,
                        null,
                        "signinController");
        Customer customer = signinController.getCustomer();
        try {
            Item item = itemBeanLocal.findItem(id);
            item.setBuyer(customer);
            item.setStatus(statusReserved);
            itemBeanLocal.editItem(item);
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
