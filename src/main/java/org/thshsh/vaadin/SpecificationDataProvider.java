package org.thshsh.vaadin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.BackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

public class SpecificationDataProvider<T> extends AbstractBackEndDataProvider<T,Specification<T>> implements BackEndDataProvider<T, Specification<T>>{
    
    private static final long serialVersionUID = -4502566155677348939L;

    public static final Logger LOGGER = LoggerFactory.getLogger(SpecificationDataProvider.class); 
    
    /**
     * Repository
     */
    protected JpaSpecificationExecutor<T> repository;

    protected Specification<T> emptySpecification;


    public SpecificationDataProvider(JpaSpecificationExecutor<T> r,List<QuerySortOrder> defaultSort) {
        this.repository = r;
        if(defaultSort!=null)this.setSortOrders(defaultSort);
        this.emptySpecification = Specification.where(null);
    }

    public static <T> Specification<T> combineFilters(Specification<T> query,Specification<T> filter){
        if(query == null) return filter;
        else if(filter == null) return query;
        else return query.and(filter);
    }
    
    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, Specification<T>> query) {
        //LOGGER.info("fetchFromBackEnd sort: {}",query.getSortOrders().stream().map(qso -> qso.getSorted()).collect(Collectors.joining(",")));
        return repository.findAll(query.getFilter().orElse(emptySpecification),ChunkRequest.of(query)).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<T, Specification<T>> query) {
        return Math.toIntExact(repository.count(query.getFilter().orElse(emptySpecification)));
    }

}
