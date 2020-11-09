package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.exceptions.DatabaseException;

import de.java2enterprise.onlineshop.ejb.CustomerBeanLocal;
import de.java2enterprise.onlineshop.ejb.ItemBeanLocal;
import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class RegisterController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @PersistenceContext
    private EntityManager em;
    
    @Inject
    private Customer customer;
    
    @EJB
    private CustomerBeanLocal customerBeanLocal;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;

    public String registerCustomer() {
    	try {
    		customerBeanLocal.persistCustomer(customer);
    		FacesMessage m = new FacesMessage(
                "Succesfully registered new user!",
                "User " + customer.getEmail() + " has registered with ID " + customer.getId() + ".");
            FacesContext
                .getCurrentInstance()
                .addMessage("registerForm", m);
            return "signin";
    	}
    	catch(Exception e) {
    		//TODO catch SQLIntegrityConstraintViolationException, doesn't work yet
    		if (e instanceof DatabaseException) {
        		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "A user with this e-mail already exists!",
                    "A user with e-mail " + customer.getEmail() + " already exists. Please choose another one.");
                FacesContext
                    .getCurrentInstance()
                    .addMessage("registerForm", m);
        	}
    		FacesMessage m = new FacesMessage(
    			FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
            FacesContext
                .getCurrentInstance()
                .addMessage("registerForm", m);
            
            return "register";
    	}
    }

    public String deregisterCustomer(SigninController signinController) {
    	try {
    		Customer customer = signinController.getCustomer();
    		Status statusActive = statusBeanLocal.findStatus(1L);
            Status statusSold = statusBeanLocal.findStatus(3L);
            Status statusReserved = statusBeanLocal.findStatus(4L);
            
            //find active items to delete
    		TypedQuery<Item> queryActiveItems = em.createQuery(
	                "FROM " + Item.class.getSimpleName() + " i "
	                        + "WHERE (i.status= :statusActive "
	                		+ "OR i.status= :statusReserved) "
	                        + "AND i.seller= :seller",
	                Item.class);
    		queryActiveItems.setParameter("seller", customer);
    		queryActiveItems.setParameter("statusActive", statusActive);
    		queryActiveItems.setParameter("statusReserved", statusReserved);
	        List<Item> activeItems = queryActiveItems.getResultList();
	        if(activeItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                "No sold and active items found!",
	                "No sold and active items found belonging to customer " + customer.getEmail());
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("welcomeForm", m);
	        } else {
	        	for(int i = 0; i < activeItems.size(); i++) {
	        		Item item = activeItems.get(i);
	        		item.setSeller(null);
	        		itemBeanLocal.removeItem(item);
	        	}
	    		FacesMessage m = new FacesMessage(
	                "Succesfully cleand up sold and active items of user!",
	                "Cleand up sold and active items of user by setting seller to null.");
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("welcomeForm", m);
	        }
	        
	        //find bought items to clean up
	        TypedQuery<Item> queryBought = em.createQuery(
	                "FROM " + Item.class.getSimpleName() + " i "
	                        + "WHERE i.status= :statusSold "
	                        + "AND i.buyer= :buyer",
	                Item.class);
	        queryBought.setParameter("buyer", customer);
	        queryBought.setParameter("statusSold", statusSold);
	        List<Item> boughtItems = queryBought.getResultList();
	        if(boughtItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                "No bought items found!",
	                "No bought items found belonging to customer " + customer.getEmail());
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("welcomeForm", m);
	        } else {
	        	for(int i = 0; i < boughtItems.size(); i++) {
	        		Item item = boughtItems.get(i);
	        		item.setBuyer(null);
	        		itemBeanLocal.editItem(item);
	        	}
	    		FacesMessage m = new FacesMessage(
	                "Succesfully cleand up bought items of user!",
	                "Cleand up bought items of user by setting buyer to null.");
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("welcomeForm", m);
	        }
	        
	      //find reserved items to clean up
	        TypedQuery<Item> queryReserved = em.createQuery(
	                "FROM " + Item.class.getSimpleName() + " i "
	                        + "WHERE i.status= :statusReserved "
	                        + "AND i.buyer= :buyer",
	                Item.class);
	        queryReserved.setParameter("buyer", customer);
	        queryReserved.setParameter("statusReserved", statusReserved);
	        List<Item> reservedItems = queryReserved.getResultList();
	        if(reservedItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                "No bought items found!",
	                "No bought items found belonging to customer " + customer.getEmail());
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("welcomeForm", m);
	        } else {
	        	for(int i = 0; i < reservedItems.size(); i++) {
	        		Item item = reservedItems.get(i);
	        		item.setBuyer(null);
	        		itemBeanLocal.editItem(item);
	        	}
	    		FacesMessage m = new FacesMessage(
	                "Succesfully cleand up reserved items of user!",
	                "Cleand up reserved items of user by setting buyer to null.");
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("welcomeForm", m);
	        }
	        
	        //find sold items to clean up
	        TypedQuery<Item> querySold = em.createQuery(
	                "FROM " + Item.class.getSimpleName() + " i "
	                        + "WHERE i.status= :statusSold "
	                        + "AND i.seller= :seller",
	                Item.class);
	        querySold.setParameter("seller", customer);
	        querySold.setParameter("statusSold", statusSold);
	        List<Item> soldItems = querySold.getResultList();
	        if(soldItems.isEmpty()) {
	            FacesMessage m = new FacesMessage(
	                "No sold items found!",
	                "No sold items found belonging to customer " + customer.getEmail());
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("welcomeForm", m);
	        } else {
	        	for(int i = 0; i < soldItems.size(); i++) {
	        		Item item = soldItems.get(i);
	        		item.setSeller(null);
	        		itemBeanLocal.editItem(item);
	        	}
	    		FacesMessage m = new FacesMessage(
	                "Succesfully cleand up bought items of user!",
	                "Cleand up bought items of user by setting buyer to null.");
	            FacesContext
	                .getCurrentInstance()
	                .addMessage("welcomeForm", m);
	        }
	        
	        //TODO show deregister message
	        return customerBeanLocal.removeCustomer(customer);
	        
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
                FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
            FacesContext
                .getCurrentInstance()
                .addMessage("welcomeForm", m);
            //TODO show deregister message
            return "failCustomerRemove";
    	}
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
