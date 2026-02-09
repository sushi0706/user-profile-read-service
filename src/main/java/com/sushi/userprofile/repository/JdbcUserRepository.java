package com.sushi.userprofile.repository;

import com.sushi.userprofile.model.User;
import com.sushi.userprofile.service.UserNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(rs.getString("id"), rs.getString("name"));

    @Override
    public User findById(String id) {
        String sql = "select id, name from users where id = ?";
        return jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, id);
    }

    @Override
    public void update(User user) {
        String sql = "update users set name = ? where id = ?";
        int rowUpdated = jdbcTemplate.update(sql, user.getName(), user.getId());
        if (rowUpdated == 0) {
            throw new UserNotFoundException(user.getId());
        }
    }
}
