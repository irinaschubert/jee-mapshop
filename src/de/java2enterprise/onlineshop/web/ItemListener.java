package de.java2enterprise.onlineshop.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;


public class ItemListener implements DataModelListener{
		private final Logger log = Logger.getLogger(getClass().getName());
		
		@Override
		public void rowSelected(DataModelEvent event) {
			log.log(Level.INFO, event.getRowIndex() + ": " + event.getRowData());
		}
}
