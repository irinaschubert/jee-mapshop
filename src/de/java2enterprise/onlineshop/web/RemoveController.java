package de.java2enterprise.onlineshop.web;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import de.java2enterprise.onlineshop.ejb.RemoveBeanLocal;
import de.java2enterprise.onlineshop.ejb.SellBeanLocal;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class RemoveController implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction ut;
    
    @EJB
    private SellBeanLocal sellBeanLocal;
    
    @EJB
    private RemoveBeanLocal removeBeanLocal;
    
    /*public String removeItem(Long id) {
        try {
            ut.begin();
            Item item = em.find(Item.class, id);
            removeBeanLocal.removeItem(item);
            ut.commit();
            log.info(item.getTitle() + " removed.");
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage(
                            "accountForm",
                            m);
        }
        return "/account.jsf";
    }*/


    public String deactivateItem(Long id) {
        try {
        	ut.begin();
        	Item item = em.find(Item.class, id);
            Status status = em.find(Status.class, 2L); //inactive
            item.setStatus(status);
            removeBeanLocal.deactivateItem(item);
            ut.commit();
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
        try {
        	ut.begin();
        	Item item = em.find(Item.class, id);
            Status status = em.find(Status.class, 1L); //active
            item.setStatus(status);
            removeBeanLocal.removeItemFromCart(item);
            ut.commit();
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
