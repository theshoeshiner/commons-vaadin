package org.thshsh.vaadin.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * FIXME this is less about string searching and more about configuring the functions
 * @author daniel.watson
 *
 * @param <T>
 * @param <ID>
 */

public class StringSearchDataProvider<T> extends CustomCallbackDataProvider<T,String> {
    
    private static final long serialVersionUID = -7345980579244887692L;
        
    private static final Logger LOGGER = LoggerFactory.getLogger(StringSearchDataProvider.class);
    
    public StringSearchDataProvider(StringSearchRepository<T, ?> repository,List<QuerySortOrder> defaultSort) {
        super(repository::findAllByString,repository::countByString,null,defaultSort);
        
    }
    
    public String combineStringFilters(String query,String config){
        LOGGER.debug("combined filter query/config: {} + {}",query,config);
        if(query == null) return config;
        else if(config == null) return query;
        else throw new IllegalStateException("Cannot combine query and configured filter");
    }

}
