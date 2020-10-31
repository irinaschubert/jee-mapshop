package de.java2enterprise.onlineshop.web;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import de.java2enterprise.onlineshop.ejb.RegisterBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;

@Named
@RequestScoped
public class RegisterController implements Serializable {    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private Customer customer;   
    
    @EJB
    private RegisterBeanLocal registerBeanLocal;

    public String persist() {
    	try {
    		registerBeanLocal.persist(customer);
    		FacesMessage m = new FacesMessage(
                    "Succesfully signed in!",
                    "You signed in under id " + customer.getId());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("registerForm", m);
    	}catch(Exception e) {
    		FacesMessage fm = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage(
                            "registerForm",
                            fm);
    	}
        return "register";
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
