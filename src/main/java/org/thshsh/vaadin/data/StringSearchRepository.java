package org.thshsh.vaadin.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StringSearchRepository<T, ID> extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {

    public Page<T> findAllByString(String s,Pageable p);
    
    public Long countByString(String s);
    
    /*public default Page<T> findAllByString(String s,Pageable p) {
    	throw new UnsupportedOperationException();
    }
    
    public default Long countByString(String s) {
    	throw new UnsupportedOperationException();
    }*/
	
}
