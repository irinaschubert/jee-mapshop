package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
            	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            	String errorLogin = ResourceBundle.getBundle("messages",locale).getString("errorLogin");
            	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
                FacesMessage m = new FacesMessage(
                		FacesMessage.SEVERITY_WARN,
                		errorLogin,
                        tryAgain);
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
        	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        	String error = ResourceBundle.getBundle("messages",locale).getString("error");
        	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("signinForm", m);
            return "signin.jsf";
        }
    }
    
    public String signout() {
    	this.setCustomer(null);
    	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    	FacesMessage m = new FacesMessage(
                "Successfully signed out!",
                "Goodbye " + customer.getEmail() + ".");
        FacesContext
                .getCurrentInstance()
                .addMessage("signinForm", m);
    	return "signin.jsf";
    }
    
    public void emailChanged(ValueChangeEvent event) {
    	String email = (String) event.getNewValue();
    	try {
    		customer.setEmail(email);
    		customerBeanLocal.editCustomer(customer);
    		FacesMessage m = new FacesMessage(
                    "Successfully updated profile!",
                    "Your old email is no longer valid.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}catch(Exception e) {
    		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        	String error = ResourceBundle.getBundle("messages",locale).getString("error");
        	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
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
                    "Successfully updated profile!",
                    "Your old password is no longer valid.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}catch(Exception e) {
    		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        	String error = ResourceBundle.getBundle("messages",locale).getString("error");
        	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}
    }
}
