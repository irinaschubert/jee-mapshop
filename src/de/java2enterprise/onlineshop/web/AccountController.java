package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.java2enterprise.onlineshop.ejb.SellBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;

@Named
@RequestScoped
public class AccountController implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger log = Logger.getLogger(AccountController.class.toString());

    @PersistenceContext
    private EntityManager em;

    private List<Item> items;
    private List<Item> offeredItems;
    private List<Item> boughtItems;
    
    @EJB
    private SellBeanLocal sellBeanLocal;

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
            log.severe(e.getMessage());
        }
        return new ArrayList<Item>();
    }
    
    public List<Item> findOfferedItems(SigninController signinController) {
    	Customer customer = signinController.getCustomer();
        customer = sellBeanLocal.findCustomer(customer.getId());
    	System.out.println("customer is: " + customer.getEmail());
    	try {
            TypedQuery<Item> query = em.createQuery(
                    "SELECT i FROM Item i "
                            + "WHERE i.seller= :seller ",
                    Item.class);
            query.setParameter("seller", customer);
            offeredItems = query.getResultList();
            if(offeredItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                        "No offered items!",
                        "No offered items found for this user!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("signinForm", m);
            } else {
            	for(int i = 0; i < offeredItems.size(); i++) {
            	}
            	
                FacesMessage m = new FacesMessage(
                        "Success",
                        "Offered items successfully retrieved");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("accountForm", m);
            }
        } catch (Exception e) {
            FacesMessage fm = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage(
                            "accountForm",
                            fm);
        }
        return offeredItems;
    }
    
    public List<Item> findBoughtItems(SigninController signinController) {
    	Customer customer = signinController.getCustomer();
        customer = sellBeanLocal.findCustomer(customer.getId());
    	
    	try {
            TypedQuery<Item> query = em.createQuery(
                    "SELECT i FROM Item i "
                            + "WHERE i.buyer= :buyer ",
                    Item.class);
            query.setParameter("buyer", customer);
            boughtItems = query.getResultList();
            if(boughtItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                        "No bought items!",
                        "No bought items found for this user!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("signinForm", m);
            } else {
                FacesMessage m = new FacesMessage(
                        "Success",
                        "bla");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("accountForm", m);
            }
        } catch (Exception e) {
            FacesMessage fm = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage(
                            "accountForm",
                            fm);
        }
        return boughtItems;
    }
}
