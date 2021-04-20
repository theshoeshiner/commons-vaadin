package org.thshsh.vaadin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.google.common.base.Preconditions;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

public class ChunkRequest<T> implements Pageable {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ChunkRequest.class);
	
    public static <T,F> ChunkRequest<T> of(Query<T, F> q, List<QuerySortOrder> defaultSort) {
        return new ChunkRequest<T>(q.getOffset(), q.getLimit(), mapSort(q.getSortOrders(), defaultSort));
    }

    private static Sort mapSort(List<QuerySortOrder> sortOrders, List<QuerySortOrder> defaultSort) {
    	LOGGER.info("map sort: {} , {}",sortOrders,defaultSort);
        if (sortOrders == null || sortOrders.isEmpty()) {
        	return Sort.by(mapSortCriteria(defaultSort));
            //return new Sort(mapSortCriteria(defaultSort));
        } else {
        	return Sort.by(mapSortCriteria(sortOrders));
            //return new Sort(mapSortCriteria(sortOrders));
        }
    }

    private static Sort.Order[] mapSortCriteria(List<QuerySortOrder> sortOrders) {
    	LOGGER.info("mapSortCriteria: {} , {}",sortOrders);
    	for(QuerySortOrder qso : sortOrders) LOGGER.info("qso: {}",qso.getSorted());
        return sortOrders.stream()
                .map(s -> new Sort.Order(s.getDirection() == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC, s.getSorted()))
                .toArray(Sort.Order[]::new);
    }

    private final Sort sort;
    private int limit = 0;
    private int offset = 0;

    private ChunkRequest(int offset, int limit, Sort sort) {
        Preconditions.checkArgument(offset >= 0, "Offset must not be less than zero!");
        Preconditions.checkArgument(limit > 0, "Limit must be greater than zero!");
        this.sort = sort;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return this;
    }

    @Override
    public Pageable first() {
        return this;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}