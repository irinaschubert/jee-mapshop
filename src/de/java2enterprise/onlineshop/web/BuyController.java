package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import de.java2enterprise.onlineshop.ejb.SellBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class BuyController implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger log = Logger
            .getLogger(BuyController.class.toString());

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction ut;
    
    @Inject
    private Status status;
    
    @EJB
    private SellBeanLocal sellBeanLocal;

    public String buyItem(Long id) {
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
            status = sellBeanLocal.findStatus(3L);
            Item item = em.find(Item.class, id);
            item.setBuyer(customer);
            item.setSold(LocalDateTime.now());
            item.setStatus(status);
            ut.commit();
            log.info(item + " bought by " + customer.getEmail());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return "/search.jsf";
    }
}
