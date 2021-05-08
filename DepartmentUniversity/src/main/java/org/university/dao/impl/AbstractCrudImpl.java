package org.university.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.university.dao.CrudDao;

public abstract class AbstractCrudImpl<E> implements CrudDao<E, Integer> {

    protected final JdbcTemplate jdbcTemplate;
    private final String saveQuery;
    private final String findByIdQuery;
    private final String findAllQuery;
    private final String findAllPaginationQuery;
    private final String deleteByIdQuery;

    protected AbstractCrudImpl(DataSource dataSource, String saveQuery, String getByIdQuery, String getAllQuery,
            String findAllPaginationQuery, String deleteByIdQuery) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.saveQuery = saveQuery;
        this.findByIdQuery = getByIdQuery;
        this.findAllQuery = getAllQuery;
        this.findAllPaginationQuery = findAllPaginationQuery;
        this.deleteByIdQuery = deleteByIdQuery;
    }

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

    protected abstract Object[] insert(E entity);

    protected abstract RowMapper<E> getMapper();
}
