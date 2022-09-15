package org.thshsh.vaadin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

public class QueryUtils {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(QueryUtils.class);

	/**
	 * converts query/default vaadin sort order to spring sort object, checking for empty lists in both
	 * @param sortOrders
	 * @param defaultSort
	 * @return
	 */
	public static Sort convertToSort(List<QuerySortOrder> sortOrders, List<QuerySortOrder> defaultSort) {
	    if (sortOrders == null || sortOrders.isEmpty()) {
	    	if(defaultSort == null ||  defaultSort.isEmpty()) return Sort.unsorted();
	    	else return Sort.by(convertToSortOrders(defaultSort));
	    } else {
	    	return Sort.by(convertToSortOrders(sortOrders));
	    }
	}

	public static Sort convertToSort(List<QuerySortOrder> sortOrders) {
		return convertToSort(sortOrders, null);
	}
	
	/**
	 * converts vaadin sort orders to an array of spring sort orders
	 * @param sortOrders
	 * @param defaultSort
	 * @return
	 */
	public static Sort.Order[] convertToSortOrders(List<QuerySortOrder> sortOrders) {
	    return sortOrders.stream()
	            .map(s -> new Sort.Order(s.getDirection() == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC, s.getSorted()))
	            .toArray(Sort.Order[]::new);
	}
	

}
