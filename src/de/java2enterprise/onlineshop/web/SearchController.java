package de.java2enterprise.onlineshop.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.inject.Named;

import de.java2enterprise.onlineshop.ejb.ItemBeanLocal;
import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class SearchController implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;

    //@SuppressWarnings("unused")
	//private List<Item> items;
    private String term;
    //private List<Item> resultItems = new ArrayList<Item>();
    
    public List<Item> findItems() {
    	List<Item> resultItems = new ArrayList<Item>();
    	Status statusActive = statusBeanLocal.findStatus(1L);
    	String termBefore = term;
        try {
        	if(term == null) {
        		term = "";
        	}
        	resultItems = itemBeanLocal.findItemsByQuery(statusActive, term);
        	term = termBefore;
        	if(resultItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                        "No items matching your search!",
                        "No items found!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("searchForm", m);
            } else {
            	for(int i = 0; i < resultItems.size(); i++) {
            		//TODO only display these items
            		System.out.println("Item found: " + resultItems.get(i).getTitle());
            	}
            	
                FacesMessage m = new FacesMessage(
                        "Succes!",
                        "Items successfully retrieved");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("searchForm", m);
            }
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("searchForm", m);
        }
        return resultItems;
    }
    
    /*public DataModel<Item> getItems() {
    	List<Item> list = itemBeanLocal.findAll();
    	Item[] items = list.toArray(new Item[list.size()]);
    	DataModel<Item> dataModel = new ArrayDataModel<Item>(items);
    	dataModel.addDataModelListener(new ItemListener());
    	return dataModel;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public DataModel<Item> getResultItems() {
    	List<Item> list = resultItems;
    	Item[] items = list.toArray(new Item[list.size()]);
    	DataModel<Item> dataModel = new ArrayDataModel<Item>(items);
    	dataModel.addDataModelListener(new ItemListener());
    	return dataModel;
    }

    public void setResultItems(List<Item> items) {
        this.resultItems = items;
    }*/
    
    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
