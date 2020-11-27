package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

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
    
    private final static Logger log = Logger.getLogger(SigninSignoutController.class.toString());
    
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
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	List<Customer> customers = new ArrayList<Customer>();
    	customers = customerBeanLocal.findCustomerByCredentials(customer.getEmail(), customer.getPassword());
        try {
            if(customers.isEmpty()) {
            	log.severe("Signin failed for user " + customer.getEmail());
            	
            	this.setCustomer(null);
            	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            	
            	String errorLogin = ResourceBundle.getBundle("messages",locale).getString("errorLogin");
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
            	String successDetail = ResourceBundle.getBundle("messages",locale).getString("successLoginDetail");
                FacesMessage m = new FacesMessage(
                        success,
                        successDetail);
                FacesContext
                        .getCurrentInstance()
                        .addMessage("searchForm", m);
                return "search.jsf";
            }
        } catch (Exception e) {
        	log.severe(e.getMessage());
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
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	
    	this.setCustomer(null);
    	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    	String successDetail = ResourceBundle.getBundle("messages",locale).getString("successSignoutDetail");
        FacesMessage m = new FacesMessage(
                success,
                successDetail);
        FacesContext
                .getCurrentInstance()
                .addMessage("signinForm", m);
    	return "signin.jsf";
    }
    
    public String emailChanged(ValueChangeEvent event) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	String email = (String) event.getNewValue();
    	try {
    		customer.setEmail(email);
    		customerBeanLocal.editCustomer(customer);
        	String successDetail = ResourceBundle.getBundle("messages",locale).getString("successEmailChangedDeatil");
            FacesMessage m = new FacesMessage(
                    success,
                    successDetail);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}catch(Exception e) {
    		log.severe(e.getMessage());
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}
    	return "editProfile.jsf";
    }
    
    public String passwordChanged(ValueChangeEvent event) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	String password = (String) event.getNewValue();
    	try {
    		customer.setPassword(password);
    		customerBeanLocal.editCustomer(customer);
        	String successDetail = ResourceBundle.getBundle("messages",locale).getString("successPasswordChangedDeatil");
            FacesMessage m = new FacesMessage(
                    success,
                    successDetail);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}catch(Exception e) {
    		log.severe(e.getMessage());
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editProfileForm", m);
    	}
    	return "editProfile.jsf";
    }
}
