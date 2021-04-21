package org.thshsh.vaadin;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

@SuppressWarnings("serial")
public class StringSearchDataProvider<T, ID extends Serializable> implements ConfigurableFilterDataProvider<T, String,String> , BackEndDataProvider<T, String>{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ExampleFilterDataProvider.class);	
	
    private JpaRepository<T,ID> repository;
    private List<QuerySortOrder> defaultSort;
    private ConfigurableFilterDataProvider<T, String, String> delegate;

    protected Function<Pageable,Page<T>> findAllFunction;
    protected BiFunction<String,Pageable,Page<T>> findFilteredFunction;
    protected Supplier<Long> countAllFunction;
    protected Function<String,Long> countFilteredFunction;

    
    @SuppressWarnings("unchecked")
	public StringSearchDataProvider(JpaRepository<T,ID> r, List<QuerySortOrder> defaultSort) {
 
        this.repository = r;
        this.defaultSort = defaultSort;
        delegate = buildDataProvider();
        
        //By default we use these methods, but they can be overridden by consumer
        findAllFunction = repository::findAll;
        countAllFunction = repository::count;
        
        if(repository instanceof StringSearchRepository) {        	
        	StringSearchRepository<T,ID> ssr = (StringSearchRepository<T, ID>) repository;
        	 findFilteredFunction = ssr::findByStringSearch;
        	 countFilteredFunction = ssr::countByStringSearch;
        }
        
    }
    
    
    private ConfigurableFilterDataProvider<T, String, String> buildDataProvider() {
		  CallbackDataProvider<T, String> dataProvider = DataProvider.fromFilteringCallbacks(
		        q -> q.getFilter()
		                .map(filter -> findFilteredFunction.apply(filter, ChunkRequest.of(q, defaultSort)).getContent())
		                .orElseGet(() -> findAllFunction.apply(ChunkRequest.of(q, defaultSort)).getContent())
		                .stream(),
		        q -> Ints.checkedCast(q
		                .getFilter()
		                .map(document -> countFilteredFunction.apply(document))
		                .orElseGet(countAllFunction)));
        

        return dataProvider.withConfigurableFilter((q, c) -> c);
    }
    
    public Long countAll() {
    	return countAllFunction.get();
    }

    @Override
    public void setFilter(String filter) {
        delegate.setFilter(filter);
    }

    @Override
    public boolean isInMemory() {
        return delegate.isInMemory();
    }

    @Override
    public int size(Query<T, String> query) {
        return delegate.size(query);
    }

    @Override
    public Stream<T> fetch(Query<T, String> query) {
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
    public <C> DataProvider<T, C> withConvertedFilter(SerializableFunction<C, String> filterConverter) {
        return delegate.withConvertedFilter(filterConverter);
    }

    @Override
    public <Q, C> ConfigurableFilterDataProvider<T, Q, C> withConfigurableFilter(SerializableBiFunction<Q, C, String> filterCombiner) {
        return delegate.withConfigurableFilter(filterCombiner);
    }

    @Override
    public ConfigurableFilterDataProvider<T, Void, String> withConfigurableFilter() {
        return delegate.withConfigurableFilter();
    }


	public void setFindAllFunction(Function<Pageable, Page<T>> findAllFunction) {
		this.findAllFunction = findAllFunction;
	}

	public void setFindFilteredFunction(BiFunction<String, Pageable, Page<T>> findFilteredFunction) {
		this.findFilteredFunction = findFilteredFunction;
	}

	public void setCountAllFunction(Supplier<Long> countAllFunction) {
		this.countAllFunction = countAllFunction;
	}

	public void setCountFilteredFunction(Function<String, Long> countFilteredFunction) {
		this.countFilteredFunction = countFilteredFunction;
	}

	@Override
	public void setSortOrders(List<QuerySortOrder> sortOrders) {
		throw new IllegalStateException("Not implemented!");
	}
}
