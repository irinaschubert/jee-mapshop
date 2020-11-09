package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class CartController implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;
    
    @Resource
    private UserTransaction ut;
    private List<Item> reservedItems;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    public String reserveItem(Long id) {
    	Status statusReserved;
        FacesContext ctx = FacesContext.getCurrentInstance();
        ELContext elc = ctx.getELContext();
        ELResolver elr = ctx.getApplication().getELResolver();
        SigninController signinController = (SigninController) elr
                .getValue(
                        elc,
                        null,
                        "signinController");
        Customer customer = signinController.getCustomer();
        try {
            ut.begin();
            statusReserved = statusBeanLocal.findStatus(4L);
            Item item = em.find(Item.class, id);
            item.setBuyer(customer);
            item.setStatus(statusReserved);
            ut.commit();
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
    
    public List<Item> findReservedItems(SigninController signinController) {
    	Status statusReserved;
    	Customer customer = signinController.getCustomer();
        statusReserved = statusBeanLocal.findStatus(4L);
        
    	try {
    		TypedQuery<Item> query = em.createQuery(
    				"FROM " + Item.class.getSimpleName() + " i "
                            + "WHERE i.buyer = :buyer "
                    		+ "AND i.status = :status",
                    Item.class);
    		query.setParameter("buyer", customer);
            query.setParameter("status", statusReserved);
            reservedItems = query.getResultList();
            if(reservedItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                        "No items in cart!",
                        "No items found!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("searchForm", m);
            } else {
            	for(int i = 0; i < reservedItems.size(); i++) {
            	}
            	
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
