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
import java.util.Locale;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import de.java2enterprise.onlineshop.ejb.ItemBeanLocal;
import de.java2enterprise.onlineshop.ejb.StatusBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class SellController implements Serializable {
	
    private static final long serialVersionUID = 1L;

    public final static int MAX_IMAGE_LENGTH = 300;
    
    @Inject
    private Item item;
    private Part part;
    
    @EJB
    private StatusBeanLocal statusBeanLocal;
    
    @EJB
    private ItemBeanLocal itemBeanLocal;
    
    public String sellItem(SigninSignoutController signinController) {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        String success = ResourceBundle.getBundle("messages",locale).getString("success");
    	String error = ResourceBundle.getBundle("messages",locale).getString("error");
    	String tryAgain = ResourceBundle.getBundle("messages",locale).getString("tryAgain");
    	
    	Status statusActive = statusBeanLocal.findStatus(1L);
    	Customer customer = signinController.getCustomer();
    	
        try {
        	if(part != null) {
        		InputStream input = part.getInputStream();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int length = 0; (length = input.read(buffer)) > 0;) {
                    output.write(buffer, 0, length);
                }
                item.setFoto(scale(output.toByteArray()));
        	}
        	else {
        		item.setFoto(null);
        	}
            item.setStatus(statusActive);
            item.setSeller(customer);
            Long productId = itemBeanLocal.persistItem(item);
            item.setProductId(productId);
            itemBeanLocal.editItem(item);
            
            item = null;
            
        	String successDetail = ResourceBundle.getBundle("messages",locale).getString("successItemCreationDeatil");
            FacesMessage m = new FacesMessage(
                    success,
                    successDetail);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("sellForm", m);
        } catch (Exception e) {
            FacesMessage m = new FacesMessage(
            		FacesMessage.SEVERITY_ERROR,
                    error,
                    tryAgain);
            FacesContext
                    .getCurrentInstance()
                    .addMessage("sellForm", m);
        }
        return "sell.jsf";
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
    
    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }
    
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
