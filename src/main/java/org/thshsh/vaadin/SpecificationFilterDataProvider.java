package org.thshsh.vaadin;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.BackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * Data provider that can filter by an example entity, should eventually move this to common project
 * @author TheShoeShiner
 * @param <T>
 * @param <ID>
 */
@SuppressWarnings("serial")
public class SpecificationFilterDataProvider<T> extends AbstractBackEndDataProvider<T,Specification<T>> implements BackEndDataProvider<T, Specification<T>>{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(SpecificationFilterDataProvider.class);	
	
	protected JpaSpecificationExecutor<T> repository;

    protected Function<Specification<T>,Long> countFunction;
    protected BiFunction<Specification<T>,Pageable,Page<T>> findFunction;

    protected Specification<T> baseSpecification;

    public SpecificationFilterDataProvider(JpaSpecificationExecutor<T> r,List<QuerySortOrder> defaultSort) {
    	
        this.repository = r;

        if(defaultSort!=null)this.setSortOrders(defaultSort);

        findFunction = repository::findAll;
        countFunction = repository::count;
        
    }

    public void setCountFunction(Function<Specification<T>, Long> countFilteredFunction) {
		this.countFunction = countFilteredFunction;
	}

	public void setFindFunction(BiFunction<Specification<T>, Pageable, Page<T>> findFilteredFunction) {
		this.findFunction = findFilteredFunction;
	}

	protected Specification<T> getCombinedSpecification(Optional<Specification<T>> filter){
    	LOGGER.debug("getCombinedSpecification: {}",filter);
    	if(!filter.isPresent()) return baseSpecification;
    	else if(baseSpecification != null) return baseSpecification.and(filter.get());
    	else return filter.get();
    }
    
    protected Optional<Specification<T>> getBaseSpecification() {
    	return Optional.ofNullable(baseSpecification);
    }
    
    public void setBaseSpecification(Specification<T> noFilter) {
    	this.baseSpecification = noFilter;
    }

    @Override
    public boolean isInMemory() {
    	return false;
    }

	/* @Override
	public int size(Query<T, Specification<T>> query) {
		
		return (int) repository.count(getCombinedSpecification(query.getFilter()));
		
		
	}*/

	/* @Override
	public Stream<T> fetch(Query<T, Specification<T>> query) {
		//ChunkRequest.of(q, defaultSort)).getContent()
		return repository.findAll(getCombinedSpecification(query.getFilter()), ChunkRequest.of(query, defaultSort)).stream();
		
	    //return delegate.fetch(query);
	}*/

	@Override
	protected Stream<T> fetchFromBackEnd(Query<T, Specification<T>> query) {
		return repository.findAll(getCombinedSpecification(query.getFilter()), ChunkRequest.of(query, null)).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<T, Specification<T>> query) {
		return (int) repository.count(getCombinedSpecification(query.getFilter()));
	}

	/* @Override
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
	*/
	/* @Override
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
	}*/

    
	/* public interface Finder<T,ID> {
		public Page<T> find(PagingAndSortingRepository<T, ID> repo,Example<T> ex,Pageable p);
		public Page<T> find(PagingAndSortingRepository<T, ID> repo,Pageable p);
		public Long count(PagingAndSortingRepository<T, ID> repo,Example<T> ex);
	}*/

	/*	@Override
		public void setSortOrders(List<QuerySortOrder> sortOrders) {
			this.defaultSort = sortOrders;
		}*/

	/*@Override
	public void setFilter(Specification<T> filter) {
		// TODO Auto-generated method stub
		
	}*/

}
