package org.thshsh.vaadin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.google.common.base.Preconditions;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * This is necessary because Vaadin changes the page size dynamically
 * which means Spring cannot use the page size + index to reliably calculate the offset
 * The next and previous methods are never used by Vaadin data providers
 *
 * @param <T>
 */
public class ChunkRequest<T> implements Pageable {

	public static final Logger LOGGER = LoggerFactory.getLogger(ChunkRequest.class);


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
    	throw new UnsupportedOperationException();
    }

    @Override
    public Pageable previousOrFirst() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public Pageable first() {
        return this;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

	@Override
	public Pageable withPage(int pageNumber) {
		throw new UnsupportedOperationException();
	}
	
	public static ChunkRequest<?> of(int offset, int limit, Sort sort) {
	    return new ChunkRequest<>(offset,limit,sort);
	}

	public static ChunkRequest<?> of(Query<?, ?> q, Sort sort) {
	    return ChunkRequest.of(q.getOffset(), q.getLimit(),sort);
	}
	
	public static ChunkRequest<?> of(Query<?, ?> q) {
	    return ChunkRequest.of(q.getOffset(), q.getLimit(),QueryUtils.convertToSort(q.getSortOrders()));
	}
	
	public static Pageable of(Query<?,?> q,List<QuerySortOrder> defaultSort) {
		return ChunkRequest.of(q,  QueryUtils.convertToSort(q.getSortOrders(), defaultSort));
	}
}