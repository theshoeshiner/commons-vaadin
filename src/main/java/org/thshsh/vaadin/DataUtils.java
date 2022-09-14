package org.thshsh.vaadin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

public class DataUtils {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(DataUtils.class);

	public static Sort mapSort(List<QuerySortOrder> sortOrders, List<QuerySortOrder> defaultSort) {
		//LOGGER.debug("mapSort sort: {} default: {}",sortOrders,defaultSort);
	    if (sortOrders == null || sortOrders.isEmpty()) {
	    	if(defaultSort == null ||  defaultSort.isEmpty()) return Sort.unsorted();
	    	else return Sort.by(mapSortCriteria(defaultSort));
	    } else {
	    	return Sort.by(mapSortCriteria(sortOrders));
	    }
	}

	public static Sort.Order[] mapSortCriteria(List<QuerySortOrder> sortOrders) {
	    return sortOrders.stream()
	            .map(s -> new Sort.Order(s.getDirection() == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC, s.getSorted()))
	            .toArray(Sort.Order[]::new);
	}
	
		public static Pageable pageRequestOf(Query<?,?> q,List<QuerySortOrder> defaultSort) {
			//LOGGER.debug("pageableOf query page: {} limit: {} offset: {} pagesize: {} sort: {}",q.getPage(),q.getLimit(),q.getOffset(),q.getPageSize(),q.getSortOrders());
			q.getSortOrders().stream().findFirst().ifPresent(qso -> {
				//LOGGER.debug("sorted: {}",qso.getSorted());
			});
			return PageRequest.of(q.getPage(), q.getLimit(), DataUtils.mapSort(q.getSortOrders(), defaultSort));
		}
	
	public static Pageable pageableOf(Query<?,?> q,List<QuerySortOrder> defaultSort) {
		//LOGGER.debug("pageableOf query page: {} limit: {} offset: {} pagesize: {} sort: {}",q.getPage(),q.getLimit(),q.getOffset(),q.getPageSize(),q.getSortOrders());
		q.getSortOrders().stream().findFirst().ifPresent(qso -> {
			//LOGGER.debug("sorted: {}",qso.getSorted());
		});
		return ChunkRequest.of(q,  DataUtils.mapSort(q.getSortOrders(), defaultSort));
	}
}
