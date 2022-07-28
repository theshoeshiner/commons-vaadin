package org.thshsh.vaadin;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.google.common.primitives.Ints;
import com.vaadin.flow.data.provider.BackEndDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
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
public class  ExampleFilterDataProvider<T, ID extends Serializable> implements ConfigurableFilterDataProvider<T, T, T> , BackEndDataProvider<T, T>{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ExampleFilterDataProvider.class);	
	
    private ExampleFilterRepository<T,ID> repository;
    private ExampleMatcher matcher;
    private List<QuerySortOrder> defaultSort;
    private ConfigurableFilterDataProvider<T, T, T> delegate;

    protected Supplier<Long> countAllFunction;
    protected Function<Pageable,Page<T>> findAllFunction;
    
    protected Function<Example<T>,Long> countFilteredFunction;
    protected BiFunction<Example<T>,Pageable,Page<T>> findFilteredFunction;
    
    protected Function<Example<T>,Long> countNoFilteredFunction;
    protected BiFunction<Example<T>,Pageable,Page<T>> findNoFilteredFunction;
    
    protected T noFilterEntity;
    protected Example<T> noFilterExample;

    public ExampleFilterDataProvider(ExampleFilterRepository<T,ID> r,
                                     ExampleMatcher matcher,
                                     List<QuerySortOrder> defaultSort) {
       
    	//Preconditions.checkNotNull(defaultSort);  
        //Preconditions.checkArgument(defaultSort.size() > 0,"At least one sort property must be specified!");

        this.repository = r;
        this.matcher = matcher;
        this.defaultSort = defaultSort;
        delegate = buildDataProvider();
        
        findAllFunction = repository::findAll;
        countAllFunction = repository::count;
        
        findNoFilteredFunction = repository::findAll;
        countNoFilteredFunction = repository::count;
        
        findFilteredFunction = repository::findAll;
        countFilteredFunction = repository::count;
    }

    private ConfigurableFilterDataProvider<T, T, T> buildDataProvider() {

        CallbackDataProvider<T, T> dataProvider = DataProvider.fromFilteringCallbacks(
		        q -> q.getFilter()
		                .map(filter -> findFilteredFunction.apply(buildExample(filter), ChunkRequest.of(q, defaultSort)).getContent())
		                .orElseGet(() -> (noFilterEntity==null)?
		                		findAllFunction.apply(ChunkRequest.of(q, defaultSort)).getContent() : 
		                		findNoFilteredFunction.apply(noFilterExample, ChunkRequest.of(q, defaultSort)).getContent()
		                )
		                .stream(),
		        q -> Ints.checkedCast(q
		                .getFilter()
		                .map(filter -> countFilteredFunction.apply(buildExample(filter)))
		                .orElseGet(() -> (noFilterEntity==null)? 
		                		countAllFunction.get() : 
		                		countFilteredFunction.apply(noFilterExample) 
		                )));
        
        return dataProvider.withConfigurableFilter((q, c) -> c);
    }
  
   
    protected Example<T> buildExample(T probe) {
        return Example.of(probe, matcher);
    }

    @Override
    public void setFilter(T filter) {
        delegate.setFilter(filter);
    }
    
    public void setNoFilter(T noFilter) {
    	this.noFilterEntity = noFilter;
    	this.noFilterExample = (noFilterExample == null) ? null : buildExample(noFilterEntity);
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

    
    public interface Finder<T,ID> {
    	public Page<T> find(PagingAndSortingRepository<T, ID> repo,Example<T> ex,Pageable p);
    	public Page<T> find(PagingAndSortingRepository<T, ID> repo,Pageable p);
    	public Long count(PagingAndSortingRepository<T, ID> repo,Example<T> ex);
    }

	@Override
	public void setSortOrders(List<QuerySortOrder> sortOrders) {
		throw new IllegalStateException("Not implemented!");
	}

}
