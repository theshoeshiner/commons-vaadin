package org.thshsh.vaadin.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StringSearchRepository<T, ID> {

    Page<T> findAllByString(String s,Pageable p);
    
    Long countByString(String s);
  
}
