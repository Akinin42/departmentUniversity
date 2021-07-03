package org.university.dao.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.university.dao.CrudDao;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public abstract class AbstractCrudImpl<E> implements CrudDao<E, Integer> {

    protected JdbcTemplate jdbcTemplate;
    String saveQuery;
    String findByIdQuery;
    String findAllQuery;
    String findAllPaginationQuery;
    String deleteByIdQuery;
    String updateQuery;

    @Override
    public void save(E entity) {
        jdbcTemplate.update(saveQuery, insert(entity));
    }

    @Override
    public Optional<E> findById(Integer id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(findByIdQuery, getMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<E> findAll() {
        return jdbcTemplate.query(findAllQuery, getMapper());
    }

    @Override
    public List<E> findAll(int limit, int offset) {
        return jdbcTemplate.query(findAllPaginationQuery, getMapper(), limit, offset);
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update(deleteByIdQuery, id);
    }
    
    @Override
    public void update(E entity) {
        jdbcTemplate.update(updateQuery, updateArgs(entity));
    }

    protected abstract Object[] insert(E entity);

    protected abstract RowMapper<E> getMapper();
    
    protected abstract Object[] updateArgs(E entity);
}
