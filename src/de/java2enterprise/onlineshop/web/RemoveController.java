package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
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

    private final static Logger log = Logger.getLogger(RemoveController.class.toString());
    
    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction ut;
    
    @EJB
    private SellBeanLocal sellBeanLocal;
    @EJB
    private RemoveBeanLocal removeBeanLocal;
    
    public String removeItem(Long id) {
        try {
            ut.begin();
            Item item = em.find(Item.class, id);
            removeBeanLocal.removeItem(item);
            ut.commit();
            log.info(item.getTitle() + " removed.");
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return "/account.jsf";
    }


    public String deactivateItem(Long id) {
        try {
        	ut.begin();
        	Item item = em.find(Item.class, id);
            Status status = em.find(Status.class, 2L);
            item.setStatus(status);
            removeBeanLocal.deactivateItem(item);
            ut.commit();
            log.info("item deactivated.");
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return "/account.jsf";
    }
}
