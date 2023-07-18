package org.thshsh.vaadin.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SecuredStringSearchRepository<T,ID>  extends StringSearchRepository<T, ID> {

    public Page<T> findByStringSecured(String s, Pageable p);
    
    public Long countByStringSecured(String s);
    
    public Page<T> findAllSecured(Pageable p);
    
    public  Long countSecured();

    @Override
    default Page<T> findAll(Pageable pageable) {
        return findAllSecured(pageable);
    }

    @Override
    default long count() {
       return countSecured();
    }

    @Override
    default Page<T> findAllByString(String s, Pageable p) {
        return findByStringSecured(s, p);
    }

    @Override
    default Long countByString(String s) {
        return countByStringSecured(s);
    }
    
    
    
}
