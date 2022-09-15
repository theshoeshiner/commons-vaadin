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
 * Data provider that can filter by a JPA specification query
 * default sorting properties are automatically mixed in to the query by the base class
 * @author Dan
 *
 * @param <T>
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


	@Override
	protected Stream<T> fetchFromBackEnd(Query<T, Specification<T>> query) {
		return repository.findAll(getCombinedSpecification(query.getFilter()),ChunkRequest.of(query)).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<T, Specification<T>> query) {
		return (int) repository.count(getCombinedSpecification(query.getFilter()));
	}


}
