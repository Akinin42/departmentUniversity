package org.university.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao <E, ID> {
    
    void save(E entity);
    
    Optional<E> findById(ID id);
    
    List<E> findAll();
    
    List<E> findAll(int limit, int offset);
    
    void deleteById(ID id);       
}
