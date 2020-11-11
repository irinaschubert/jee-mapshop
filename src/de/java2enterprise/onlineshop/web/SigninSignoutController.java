package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import de.java2enterprise.onlineshop.ejb.CustomerBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;

@Named
@SessionScoped
public class SigninSignoutController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @Inject
    private Customer customer;
    
    @EJB
    private CustomerBeanLocal customerBeanLocal;
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String signin() {
    	List<Customer> customers = new ArrayList<Customer>();
    	customers = customerBeanLocal.findCustomerByCredentials(customer.getEmail(), customer.getPassword());
        try {
            if(customers.isEmpty()) {
            	this.setCustomer(null);
                FacesMessage m = new FacesMessage(
                		FacesMessage.SEVERITY_ERROR,
                        "Signing in was not successful!",
                        "Sorry, try again!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("signinForm", m);
                return "signin.jsf";
            } else {
                customer = customers.get(0);
                FacesMessage m = new FacesMessage(
                        "Successfully signed in!",
                        "Welcome " + customer.getEmail() + ".");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("searchForm", m);
                return "search.jsf";
            }
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    "Signing in was not successful!",
                    "Sorry, try again!");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("signinForm", m);
            return "signin.jsf";
        }
    }
    
    public String signout() {
    	this.setCustomer(null);
    	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    	return "signin.jsf";
    }
    
    public void emailChanged(ValueChangeEvent event) {
    	String email = (String) event.getNewValue();
    	try {
    		customer.setEmail(email);
    		customerBeanLocal.editCustomer(customer);
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
    	try {
    		customer.setPassword(password);
    		customerBeanLocal.editCustomer(customer);
    		FacesMessage m = new FacesMessage(
                    "Successfully changed profile!",
                    "Profile has been successfully updated.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
    				FacesMessage.SEVERITY_WARN,
                    "Couldn't change profile!",
                    "Please try again.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}
    }
}
