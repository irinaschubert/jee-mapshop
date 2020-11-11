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
import javax.inject.Named;

import de.java2enterprise.onlineshop.ejb.ItemBeanLocal;
import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@SessionScoped
public class SearchController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;

    private String term;
    private String termBefore;
    
    public List<Item> findItems() {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String warning = ResourceBundle.getBundle("messages",locale).getString("warning");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	List<Item> resultItems = new ArrayList<Item>();
    	Status statusActive = statusBeanLocal.findStatus(1L);
    	termBefore = term;
        try {
        	if(term == null) {
        		term = "";
        	}
        	resultItems = itemBeanLocal.findItemsByQuery(statusActive, term);
        	term = termBefore;
        	if(resultItems.isEmpty()) {
        		String warnDetail = ResourceBundle.getBundle("messages",locale).getString("warnSearchDetail");
                FacesMessage m = new FacesMessage(
                		FacesMessage.SEVERITY_WARN,
                        warning,
                        warnDetail);
                FacesContext
                        .getCurrentInstance()
                        .addMessage("searchForm", m);
            }
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("searchForm", m);
        }
        return resultItems;
    }
    
    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
