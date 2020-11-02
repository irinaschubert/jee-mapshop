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
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import de.java2enterprise.onlineshop.ejb.SellBeanLocal;
import de.java2enterprise.onlineshop.model.Customer;
import de.java2enterprise.onlineshop.model.Item;
import de.java2enterprise.onlineshop.model.Status;

@Named
@RequestScoped
public class SellController implements Serializable {
    private static final long serialVersionUID = 1L;

    public final static int MAX_IMAGE_LENGTH = 400;
    
    @Inject
    private Item item;
    private Part part;
    private Status status;
    
    @EJB
    private SellBeanLocal sellBeanLocal;
    
    public String persist(SigninController signinController) {
        try {
            InputStream input = part.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length = 0; (length = input.read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }
            item.setFoto(scale(output.toByteArray()));
            status = sellBeanLocal.findStatus(1L); //active
            item.setStatus(status);
            
            Customer customer = signinController.getCustomer();
            customer = sellBeanLocal.findCustomer(customer.getId());
            item.setSeller(customer);
            
            String msg = sellBeanLocal.persist(item);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
            return "sell";
        } catch (Exception e) {
        	FacesMessage m = new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    e.getMessage(),
                    e.getCause().getMessage());
            FacesContext
                    .getCurrentInstance()
                    .addMessage("searchForm", m);
            return "sellFail";
        }
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
}
