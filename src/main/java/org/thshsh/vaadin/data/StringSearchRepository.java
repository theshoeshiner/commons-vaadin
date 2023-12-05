package org.thshsh.vaadin.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StringSearchRepository<T, ID> {

    public Page<T> findAllByString(String s,Pageable p);
    
    public Long countByString(String s);
  
}
