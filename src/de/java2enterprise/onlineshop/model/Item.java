package de.java2enterprise.onlineshop.model;

import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The persistent class for the ITEM database table.
 * 
 */
@Entity
@Table(schema="MAPSHOP", name="ITEM")
@NamedQuery(name="Item.findAll", query="SELECT i FROM Item i")
public class Item implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @SequenceGenerator(
            name="ITEM_ID_GENERATOR", 
            sequenceName="SEQ_ITEM",
            schema="MAPSHOP",
            allocationSize=1,
            initialValue=1)
    @GeneratedValue(
            strategy=GenerationType.SEQUENCE, 
            generator="ITEM_ID_GENERATOR")
	
	private Long id;
	private Long product_id;
	private String title;
	private String description;
	private Long stockNumber;
	@Basic(fetch = FetchType.LAZY)
	@Lob
	private byte[] foto;
	private Double price;
	private LocalDateTime sold;
	
	//bi-directional many-to-one association to Status
	@ManyToOne
	private Status status;

	//bi-directional many-to-one association to Customer
	@ManyToOne
	private Customer seller;

	//bi-directional many-to-one association to Customer
	@ManyToOne
	private Customer buyer;

	public Item() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getProductId() {
		return this.product_id;
	}

	public void setProductId(Long productId) {
		this.product_id = productId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getStockNumber() {
		return this.stockNumber;
	}

	public void setStockNumber(Long stockNumber) {
		this.stockNumber = stockNumber;
	}
	
	public byte[] getFoto() {
		return this.foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}
	
	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
	
	public LocalDateTime getSold() {
		return this.sold;
	}

	public void setSold(LocalDateTime sold) {
		this.sold = sold;
	}
	
	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status s) {
		this.status = s;
	}


    public Customer getSeller() {
        return this.seller;
    }

    public void setSeller(Customer seller) {
        this.seller = seller;
    }

	public Customer getBuyer() {
		return this.buyer;
	}

	public void setBuyer(Customer buyer) {
		this.buyer = buyer;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
    public boolean equals(
            Object obj
    ) {
        if (
            this == obj
        ) {
            return true;
        }
        if (
            obj == null
        ) {
            return false;
        }
        if (
            !(obj instanceof Item)
        ) {
            return false;
        }
        Item other = (Item) obj;
        if (
            id == null
        ) {
            if (
                other.id != null
            ) {
                return false;
            }
        } else if (
            !id.equals(
                    other.id
            )
        ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Item [id=" + id + ", title=" + title
                + ", description=" + description + ", price=" + price + ", status="
                + status + "]";
    }

}