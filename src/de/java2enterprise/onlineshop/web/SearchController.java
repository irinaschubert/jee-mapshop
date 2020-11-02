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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.java2enterprise.onlineshop.ejb.SellBeanLocal;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class SearchController implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private SellBeanLocal sellBeanLocal;

    private List<Item> items;
    private Status status;
    private List<Item> activeItems;
    private List<Item> resultItems;
    private String term;
    
    public List<Item> findAll() {
        try {
            TypedQuery<Item> query = em.createNamedQuery(
                            "Item.findAll",
                            Item.class);
            return query.getResultList();
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                FacesMessage.SEVERITY_WARN,
                e.getMessage(),
                e.getCause().getMessage());
            FacesContext
                .getCurrentInstance()
                .addMessage("searchForm", m);
        }
        return new ArrayList<Item>();
    }
    
    public List<Item> findActiveItems() {
        status = sellBeanLocal.findStatus(1L); //active
        
    	try {
            TypedQuery<Item> query = em.createQuery(
            		"FROM " + Item.class.getSimpleName() + " i "
                            + "WHERE i.status = :status",
                    Item.class);
            query.setParameter("status", status);
            activeItems = query.getResultList();
            if(activeItems.isEmpty()) {
                FacesMessage m = new FacesMessage(
                        "No items!",
                        "No items found!");
                FacesContext
                        .getCurrentInstance()
                        .addMessage("searchForm", m);
            } else {
            	for(int i = 0; i < activeItems.size(); i++) {
            	}
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
        status = sellBeanLocal.findStatus(1L); //active
        
    	try {
            TypedQuery<Item> query = em.createQuery(
            		"FROM " + Item.class.getSimpleName() + " i "
                            + "WHERE i.status = :status "
                    		+ "AND i.title LIKE :term "
                            + "OR i.description LIKE :term",
                    Item.class);
            query.setParameter("status", status);
            query.setParameter("term", "%"+term+"%");
            resultItems = query.getResultList();
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
    	List<Item> list = findAll();
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
