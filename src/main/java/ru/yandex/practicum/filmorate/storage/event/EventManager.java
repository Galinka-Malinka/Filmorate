package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component("eventManager")
@RequiredArgsConstructor
public class EventManager {

    private final JdbcTemplate jdbcTemplate;

    public void updateEvents(Long userId, EventType eventType, Operation operation, Long entityId) {
        String sqlForEvent = "insert into events(user_id, event_type, operation, entity_id) values(?, ?, ?, ?)";
        jdbcTemplate.update(sqlForEvent, userId, eventType.toString(), operation.toString(), entityId);
    }

    public Collection<Event> getEvents(Long id) {
        String sqlQuery = "select * from events where user_id = ? order by timestamp ";
        return jdbcTemplate.query(sqlQuery, this::mapRowEvent, id);
    }

    private Event mapRowEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .timestamp(resultSet.getTimestamp("timestamp"))
                .userId(resultSet.getLong("user_id"))
                .eventType(EventType.valueOf(resultSet.getString("event_type")))
                .operation(Operation.valueOf(resultSet.getString("operation")))
                .eventId(resultSet.getLong("event_id"))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }
}
