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
    private StatusBeanLocal sellBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;

    @SuppressWarnings("unused")
	private List<Item> items;
    private String term;
    
    public List<Item> findActiveItems() {
    	Status statusActive = sellBeanLocal.findStatus(1L);
    	List<Item> activeItems = new ArrayList<Item>();
        
    	try {
            activeItems = itemBeanLocal.findItemsByStatus(statusActive);
            if(activeItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                        "No items!",
                        "No items found!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("searchForm", m);
            } else {
                FacesMessage m = new FacesMessage(
                        "Success",
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
        return activeItems;
    }
    
    public List<Item> findItems() {
    	Status statusActive = sellBeanLocal.findStatus(1L);
        List<Item> resultItems = new ArrayList<Item>();
        try {
        	resultItems = itemBeanLocal.findItemsByQuery(statusActive, term);
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
            		System.out.println(resultItems.get(i).getTitle());
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
    
    public DataModel<Item> getItems() {
    	List<Item> list = itemBeanLocal.findAll();
    	Item[] items = list.toArray(new Item[list.size()]);
    	DataModel<Item> dataModel = new ArrayDataModel<Item>(items);
    	dataModel.addDataModelListener(new ItemListener());
    	return dataModel;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
