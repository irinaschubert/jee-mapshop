package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import de.java2enterprise.onlineshop.model.Customer;

@Named
@SessionScoped
public class SigninController implements Serializable {
	
    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction ut;
    
    @Inject
    private Customer customer;
    private String email;
    private String password;
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String signin() {
        try {
            TypedQuery<Customer> query = em.createQuery(
            		"FROM " + Customer.class.getSimpleName() + " c "
                            + "WHERE c.email= :email "
                            + "AND c.password= :password",
                    Customer.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            List<Customer> customers = query.getResultList();
            if(customers.isEmpty()) {
                FacesMessage m = new FacesMessage(
                        "Signing in was not successful!",
                        "Sorry, try again!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("signinForm", m);
            } else {
                customer = customers.get(0);
                FacesMessage m = new FacesMessage(
                        "Successfully signed in!",
                        "You id is " + customer.getId());
                FacesContext
                        .getCurrentInstance()
                        .addMessage("signinForm", m);
            }
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("signinForm", m);
        }
        return "signin.jsf";
    }
    
    public void emailChanged(ValueChangeEvent event) {
    	String email = (String) event.getNewValue();
    	customer.setEmail(email);
    	try {
    		ut.begin();
    		em.merge(customer);
    		ut.commit();
    		FacesMessage m = new FacesMessage(
                    "Successfully changed profile!",
                    "Profile has been successfully updated.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}
    }
    
    public void passwordChanged(ValueChangeEvent event) {
    	String password = (String) event.getNewValue();
    	customer.setPassword(password);
    	try {
    		ut.begin();
    		em.merge(customer);
    		ut.commit();
    		FacesMessage m = new FacesMessage(
                    "Successfully changed profile!",
                    "Profile has been successfully updated.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}
    }
}
