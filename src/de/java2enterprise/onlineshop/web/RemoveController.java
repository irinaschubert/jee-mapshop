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
import de.java2enterprise.onlineshop.model.Item;

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
}
