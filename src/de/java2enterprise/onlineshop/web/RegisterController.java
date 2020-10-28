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
    		String msg = registerBeanLocal.persist(customer);
    		FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(msg));
    	}catch(Exception e) {
    		e.printStackTrace();
    		FacesContext.getCurrentInstance().addMessage("registerForm", new FacesMessage(e.getMessage()));
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
