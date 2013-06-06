package com.eurodyn.uns.web.jsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.beanutils.PropertyUtils;

import com.eurodyn.uns.util.common.WDSLogger;

public class SortableTable {

    private static final WDSLogger logger = WDSLogger.getLogger(SortableTable.class);

    private String _sort;


    private boolean _ascending;

    protected boolean isDefaultAscending(String sortColumn) {
        return true;
    }

    public SortableTable(String defaultSortColumn) {

        _sort = defaultSortColumn;
        _ascending = isDefaultAscending(defaultSortColumn);
    }

    public void sort(String sortColumn) {
        if (sortColumn == null) {
            throw new IllegalArgumentException("Argument sortColumn must not be null.");
        }

        if (_sort.equals(sortColumn)) {
            // current sort equals new sortColumn -> reverse sort order
            _ascending = !_ascending;
        } else {
            // sort new column in default direction
            _sort = sortColumn;
            _ascending = isDefaultAscending(_sort);
        }
    }

    public String getSort() {
        return _sort;
    }

    public void setSort(String sort) {
        _sort = sort;
    }

    public boolean isAscending() {
        return _ascending;
    }

    public void setAscending(boolean ascending) {
        _ascending = ascending;
    }

    public String getHeaderTitle() {
        return "label.table.sort.notSortable";
    }


    public List sort(List list) {
        if(list == null)
            return null;
        try {
            Collections.sort(list, new Comparator() {
                public int compare(Object a, Object b) {
                    Object aValue = null;
                    Object bValue = null;
                    try {
                        aValue = PropertyUtils.getProperty(a, _sort);
                        bValue = PropertyUtils.getProperty(b, _sort);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    if (aValue instanceof String)
                        return ((String) aValue).compareTo((String)bValue);
                    if (aValue instanceof Integer)
                        return ((Integer) aValue).compareTo((Integer)bValue);
                    if (aValue instanceof Date)
                        return ((Date) aValue).compareTo((Date)bValue);
                    return -1;
                }
            });

            if (!isAscending())
                Collections.reverse(list);
        } catch (Exception e) {
            logger.error(e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "System error", null));
            list = new ArrayList();
        }
        return list;
    }

}
