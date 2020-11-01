package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

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

import de.java2enterprise.onlineshop.ejb.SellBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class CartController implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger log = Logger.getLogger(CartController.class.toString());

    @PersistenceContext
    private EntityManager em;
    
    @Resource
    private UserTransaction ut;
    
    private Status status;
    private List<Item> reservedItems;
    
    @EJB
    private SellBeanLocal sellBeanLocal;

    public String reserveItem(Long id) {
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
            status = sellBeanLocal.findStatus(4L); //reserved
            Item item = em.find(Item.class, id);
            item.setBuyer(customer);
            item.setStatus(status);
            ut.commit();
            log.info(item.getTitle() + " reserved by " + customer.getEmail());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return "/search.jsf";
    }
    
    public List<Item> findReservedItems(SigninController signinController) {
    	Customer customer = signinController.getCustomer();
        customer = sellBeanLocal.findCustomer(customer.getId());
        status = sellBeanLocal.findStatus(4L); //reserved
        
    	try {
    		TypedQuery<Item> query = em.createQuery(
    				"SELECT i FROM " + Item.class.getSimpleName() + " i "
                            + "WHERE i.buyer = :buyer "
                    		+ "AND i.status = :status",
                    Item.class);
    		query.setParameter("buyer", customer);
            query.setParameter("status", status);
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
            FacesMessage fm = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage(
                            "cartForm",
                            fm);
        }
        return reservedItems;
    }
}
