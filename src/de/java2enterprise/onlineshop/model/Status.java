package de.java2enterprise.onlineshop.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/**
 * The persistent class for the ITEM database table.
 * 
 */
@Entity
@Table(
		schema = "MAPSHOP",
		name = "STATUS"
		)
@NamedQuery(name="Status.findAll", query="SELECT s FROM Status s")
public class Status implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	private String description;

	//bi-directional one-to-many association to Item
	//@OneToMany(mappedBy="status",cascade = CascadeType.PERSIST)
	//private Set<Item> items;

	public Status() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	/*public Set<Item> getItems(){
		return this.items;
	}
	
	public void setItems(Set<Item> items) {
		this.items = items;
	}
	
	public Item addItem(Item item) {
		Set<Item> items = getItems();
		if(items == null) {
			items = new HashSet<Item>();
		}
		items.add(item);
		item.setStatus(this);

		return item;
	}

	public Item removeItem(Item item) {
		getItems().remove(item);
		item.setStatus(null);

		return item;
	}*/

    @Override
    public String toString() {
        return "Status [id=" + id + ", description=" + description + "]";
    }
}