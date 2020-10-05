package org.thshsh.vaadin;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;

/**
 * Data provider that can filter by an example entity, should eventually move this to common project
 * @author TheShoeShiner
 * @param <T>
 * @param <ID>
 */
@SuppressWarnings("serial")
public class ExampleFilterDataProvider<T, ID extends Serializable> implements ConfigurableFilterDataProvider<T, T, T> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ExampleFilterDataProvider.class);	
	
    private final JpaRepository<T, ID> repository;
    private final ExampleMatcher matcher;
    private final List<QuerySortOrder> defaultSort;
    private final ConfigurableFilterDataProvider<T, T, T> delegate;
    private Finder<T,ID> finder; 
    
    public ExampleFilterDataProvider(JpaRepository<T, ID> repository,
            ExampleMatcher matcher,
            List<QuerySortOrder> defaultSort) {
    	this(repository,matcher,defaultSort,null);
    }
    
    public ExampleFilterDataProvider(JpaRepository<T, ID> repository,
                                     ExampleMatcher matcher,
                                     List<QuerySortOrder> defaultSort,Finder<T,ID> finder) {
        Preconditions.checkNotNull(defaultSort);
        Preconditions.checkArgument(defaultSort.size() > 0,
                "At least one sort property must be specified!");

        this.repository = repository;
        this.matcher = matcher;
        this.defaultSort = defaultSort;
        this.finder = finder;
        delegate = buildDataProvider();
    }

    private ConfigurableFilterDataProvider<T, T, T> buildDataProvider() {
        CallbackDataProvider<T, T> dataProvider = DataProvider.fromFilteringCallbacks(
                q -> q.getFilter()
                        .map(document -> findAll(buildExample(document), ChunkRequest.of(q, defaultSort)).getContent())
                        .orElseGet(() -> findAll(ChunkRequest.of(q, defaultSort)).getContent())
                        .stream(),
                q -> Ints.checkedCast(q
                        .getFilter()
                        .map(document -> countAll(buildExample(document)))
                        .orElseGet(repository::count)));
        return dataProvider.withConfigurableFilter((q, c) -> c);
    }
    
    private Page<T> findAll(Pageable p) {
    	if(finder == null) return repository.findAll(p);
    	else return finder.find(repository,p);
    }
    
    private Page<T> findAll(Example<T> ex,Pageable p) {
    	if(finder == null) return repository.findAll(ex, p);
    	else return finder.find(repository,ex,p);
    }
    
    private Long countAll(Example<T> ex) {
    	if(finder == null) return repository.count(ex);
    	else return finder.count(repository,ex);
    }
    
   
    private Example<T> buildExample(T probe) {
        return Example.of(probe, matcher);
    }

    @Override
    public void setFilter(T filter) {
        delegate.setFilter(filter);
    }

    @Override
    public boolean isInMemory() {
        return delegate.isInMemory();
    }

    @Override
    public int size(Query<T, T> query) {
        return delegate.size(query);
    }

    @Override
    public Stream<T> fetch(Query<T, T> query) {
        return delegate.fetch(query);
    }

    @Override
    public void refreshItem(T item) {
        delegate.refreshItem(item);
    }

    @Override
    public void refreshAll() {
        delegate.refreshAll();
    }

    @Override
    public Object getId(T item) {
        return delegate.getId(item);
    }

    @Override
    public Registration addDataProviderListener(DataProviderListener<T> listener) {
        return delegate.addDataProviderListener(listener);
    }

    @Override
    public <C> DataProvider<T, C> withConvertedFilter(SerializableFunction<C, T> filterConverter) {
        return delegate.withConvertedFilter(filterConverter);
    }

    @Override
    public <Q, C> ConfigurableFilterDataProvider<T, Q, C> withConfigurableFilter(SerializableBiFunction<Q, C, T> filterCombiner) {
        return delegate.withConfigurableFilter(filterCombiner);
    }

    @Override
    public ConfigurableFilterDataProvider<T, Void, T> withConfigurableFilter() {
        return delegate.withConfigurableFilter();
    }

    private static class ChunkRequest implements Pageable {
    	
    	public static final Logger LOGGER = LoggerFactory.getLogger(ChunkRequest.class);
    	
        public static <T> ChunkRequest of(Query<T, T> q, List<QuerySortOrder> defaultSort) {
            return new ChunkRequest(q.getOffset(), q.getLimit(), mapSort(q.getSortOrders(), defaultSort));
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
    
    public interface Finder<T,ID> {
    	public Page<T> find(JpaRepository<T, ID> repo,Example<T> ex,Pageable p);
    	public Page<T> find(JpaRepository<T, ID> repo,Pageable p);
    	public Long count(JpaRepository<T, ID> repo,Example<T> ex);
    }

}
