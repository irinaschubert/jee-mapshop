package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class AccountController implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;

    private List<Item> items;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;

    public List<Item> getItems() {
        items = findAll();
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> findAll() {
        try {
            TypedQuery<Item> query = em.createNamedQuery(
                            "Item.findAll",
                            Item.class);
            return query.getResultList();
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
            FacesContext
                .getCurrentInstance()
                .addMessage("accountForm", m);
        }
        return new ArrayList<Item>();
    }
    
    public List<Item> findOfferedItems(SigninController signinController) {
    	List<Item> offeredItems = new ArrayList<Item>();
    	Customer customer = signinController.getCustomer();
        Status statusActive = statusBeanLocal.findStatus(1L);
        Status statusReserved = statusBeanLocal.findStatus(4L);
    	try {
            TypedQuery<Item> query = em.createQuery(
                    "FROM " + Item.class.getSimpleName() + " i "
                            + "WHERE i.seller = :seller "
                    		+ "AND (i.status = :status1 "
                            + "OR i.status = :status4)",
                    Item.class);
            query.setParameter("seller", customer);
            query.setParameter("status1", statusActive);
            query.setParameter("status4", statusReserved);
            offeredItems = query.getResultList();
            if(offeredItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                    "No offered items found.",
                    "No offered items found for user " + customer.getEmail());
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
            } else {
            	for(int i = 0; i < offeredItems.size(); i++) {
            	}
            	
                FacesMessage m = new FacesMessage(
                    "Offered items successfully retrieved.",
                    "Offered items successfully retrieved for user " + customer.getEmail());
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
    
    public List<Item> findSoldItems(SigninController signinController) {
    	List<Item> soldItems = new ArrayList<Item>();
    	Customer customer = signinController.getCustomer();
        Status statusSold = statusBeanLocal.findStatus(3L);
        
    	try {
            TypedQuery<Item> query = em.createQuery(
                    "FROM " + Item.class.getSimpleName() + " i "
                            + "WHERE i.seller = :seller "
                    		+ "AND i.status = :status3",
                    Item.class);
            query.setParameter("seller", customer);
            query.setParameter("status3", statusSold);
            soldItems = query.getResultList();
            if(soldItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                	"No sold items found.",
                    "No sold items found for user " + customer.getEmail());
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
            } else {
                FacesMessage m = new FacesMessage(
                	"Sold items successfully retrieved.",
                    "Sold items successfully retrieved for user " + customer.getEmail());
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
    
    public List<Item> findBoughtItems(SigninController signinController) {
    	List<Item> boughtItems = new ArrayList<Item>();
        Status statusSold = statusBeanLocal.findStatus(3L);
        Customer customer = signinController.getCustomer();
    	
    	try {
            TypedQuery<Item> query = em.createQuery(
                    "FROM " + Item.class.getSimpleName() + " i "
                            + "WHERE i.buyer= :buyer "
                    		+ "AND i.status= :statusSold",
                    Item.class);
            query.setParameter("buyer", customer);
            query.setParameter("statusSold", statusSold);
            boughtItems = query.getResultList();
            if(boughtItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                    "No bought items found.",
                    "No bought items found for user " + customer.getEmail());
                FacesContext
                    .getCurrentInstance()
                    .addMessage("accountForm", m);
            } else {
                FacesMessage m = new FacesMessage(
                	"Bought items successfully retrieved.",
                    "Bought items successfully retrieved for user " + customer.getEmail());
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
