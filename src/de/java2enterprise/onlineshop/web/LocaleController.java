package de.java2enterprise.onlineshop.web;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class LocaleController implements Serializable {
    private static final long serialVersionUID = 1L;

    private String lang;
    
    public LocaleController(){
    	lang = "de";
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public void change(String lang) {
        this.lang = lang;
    }
}
