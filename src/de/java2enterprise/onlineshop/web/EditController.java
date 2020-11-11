package de.java2enterprise.onlineshop.web;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import de.java2enterprise.onlineshop.ejb.ItemBeanLocal;
import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Item;

@Named
@ConversationScoped
public class EditController implements Serializable {
	
    private static final long serialVersionUID = 1L;

    public final static int MAX_IMAGE_LENGTH = 300;
    
    @Inject
    private Item item;
    private Part part;
    
    @Inject
    private Conversation conversation;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;
    
	public String editThisItem(Long id) {
		if(conversation.isTransient()) {
			conversation.begin();
			item = itemBeanLocal.findItem(id);
		}
    	return "/editItem.xhtml";
    }
    
    public void titleChanged(ValueChangeEvent event) {
    	String title = (String) event.getNewValue();
    	try {
    		item.setTitle(title);
    		itemBeanLocal.editItem(item);
    		FacesMessage m = new FacesMessage(
                    "Successfully changed item!",
                    "Item has been successfully updated.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
            if(!conversation.isTransient()) {
            	conversation.end();
            }
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
    	}
    }
    
    public void descriptionChanged(ValueChangeEvent event) {
    	String description = (String) event.getNewValue();
    	try {
    		item.setDescription(description);
    		itemBeanLocal.editItem(item);
    		FacesMessage m = new FacesMessage(
                    "Successfully changed item!",
                    "Item has been successfully updated.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
            if(!conversation.isTransient()) {
            	conversation.end();
            }
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
    	}
    }
    
    public void priceChanged(ValueChangeEvent event) {
    	Double price = (Double) event.getNewValue();
    	try {
    		item.setPrice(price);
    		itemBeanLocal.editItem(item);
    		FacesMessage m = new FacesMessage(
                    "Successfully changed item!",
                    "Item has been successfully updated.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
            if(!conversation.isTransient()) {
            	conversation.end();
            }
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
    	}
    }
    
    public void stockNumberChanged(ValueChangeEvent event) {
    	Long stockNumber = (Long) event.getNewValue();
    	try {
    		item.setStockNumber(stockNumber);
    		itemBeanLocal.editItem(item);
    		FacesMessage m = new FacesMessage(
                    "Successfully changed item!",
                    "Item has been successfully updated.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
            if(!conversation.isTransient()) {
            	conversation.end();
            }
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
    	}
    }
    
    public void fotoChanged(ValueChangeEvent event) {
    	Part part = (Part) event.getNewValue();
		try {
			InputStream input = part.getInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
	        byte[] buffer = new byte[1024];
	        for (int length = 0; (length = input.read(buffer)) > 0;) {
	            output.write(buffer, 0, length);
	        }
	        item.setFoto(scale(output.toByteArray()));
		} catch (IOException e) {
			FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
		}
    	try {
    		itemBeanLocal.editItem(item);
    		FacesMessage m = new FacesMessage(
                    "Successfully changed item!",
                    "Item has been successfully updated.");
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
    	}catch(Exception e) {
    		FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("editItemForm", m);
    	}
    }

    public byte[] scale(byte[] foto) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                foto);
        BufferedImage originalBufferedImage = ImageIO
                .read(byteArrayInputStream);

        double originalWidth = (double) originalBufferedImage
                .getWidth();
        double originalHeight = (double) originalBufferedImage
                .getHeight();
        double relevantLength = originalWidth > originalHeight
                ? originalWidth
                : originalHeight;

        double transformationScale = MAX_IMAGE_LENGTH
                / relevantLength;
        int width = (int) Math
                .round(transformationScale * originalWidth);
        int height = (int) Math.round(
                transformationScale * originalHeight);

        BufferedImage resizedBufferedImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedBufferedImage
                .createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        AffineTransform affineTransform = AffineTransform
                .getScaleInstance(transformationScale,
                        transformationScale);
        g2d.drawRenderedImage(originalBufferedImage,
                affineTransform);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedBufferedImage, "PNG", baos);
        return baos.toByteArray();
    }
    
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }
}
