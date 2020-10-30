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
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class SearchController implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger log = Logger
            .getLogger(SearchController.class.toString());

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private SellBeanLocal sellBeanLocal;

    private List<Item> items;
    private Status status;
    private List<Item> activeItems;

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
    
    public List<Item> findActiveItems() {
        status = sellBeanLocal.findStatus(1L);
        
    	try {
            TypedQuery<Item> query = em.createQuery(
                    "SELECT i FROM Item i "
                            + "WHERE i.status = :status",
                    Item.class);
            query.setParameter("status", status);
            activeItems = query.getResultList();
            if(activeItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                        "No items!",
                        "No items found!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("signinForm", m);
            } else {
            	for(int i = 0; i < activeItems.size(); i++) {
            	}
            	
                FacesMessage m = new FacesMessage(
                        "Success",
                        "Items successfully retrieved");
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
        return activeItems;
    }
}
