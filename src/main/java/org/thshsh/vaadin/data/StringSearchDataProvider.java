package org.thshsh.vaadin.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * FIXME this is less about string searching and more about configuring the functions
 * @author daniel.watson
 *
 * @param <T>
 * @param <ID>
 */

public class StringSearchDataProvider<T> extends AbstractBackEndDataProvider<T,String> {
    
    private static final long serialVersionUID = -7345980579244887692L;
        
    private static final Logger LOGGER = LoggerFactory.getLogger(StringSearchDataProvider.class);

    
    protected StringSearchRepository<T,?> repository;
    
    public StringSearchDataProvider(StringSearchRepository<T, ?> repository,List<QuerySortOrder> defaultSort) {
        super();
        this.repository = repository;
        if(defaultSort!=null)this.setSortOrders(defaultSort);
    }

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, String> query) {
        LOGGER.info("fetchFromBackEnd filter: {}",query.getFilter());
        LOGGER.info("fetchFromBackEnd sort: {}",query.getSortOrders().stream().map(qso -> qso.getSorted()).collect(Collectors.joining(",")));
        return query.getFilter()
                .map(f -> repository.findAllByString(f,ChunkRequest.of(query)))
                .orElseGet(() -> repository.findAll(ChunkRequest.of(query)))
                .stream();
    }
    
    public String combineFilters(String query,String config){
        LOGGER.info("combined filter query/config: {} + {}",query,config);
        if(query == null) return config;
        else if(config == null) return query;
        else throw new IllegalStateException("Cannot combine query and configured filter");
    }

    @Override
    protected int sizeInBackEnd(Query<T,String> query) {
        return Math.toIntExact(query.getFilter()
                .map(f -> repository.countByString(f))
                .orElseGet(() -> repository.count()));
        
    }
    
}
