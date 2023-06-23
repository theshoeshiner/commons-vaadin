package org.thshsh.vaadin.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface QueryByExampleRepository<T,ID> extends QueryByExampleExecutor<T>, CrudRepository<T, ID>{

}
