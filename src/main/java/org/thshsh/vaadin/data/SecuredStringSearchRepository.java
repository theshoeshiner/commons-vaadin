package org.thshsh.vaadin.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SecuredStringSearchRepository<T,ID>  extends StringSearchRepository<T, ID> {

    Page<T> findByStringSecured(String s, Pageable p);
    
    Long countByStringSecured(String s);
    
    Page<T> findAllSecured(Pageable p);
    
     Long countSecured();

   

    @Override
    default Page<T> findAllByString(String s, Pageable p) {
        return findByStringSecured(s, p);
    }

    @Override
    default Long countByString(String s) {
        return countByStringSecured(s);
    }
    
    
    
}
