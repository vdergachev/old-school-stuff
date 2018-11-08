package ru.glavkniga.gklients.integration.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static java.lang.Character.toLowerCase;

/**
 * Created by Vladimir on 17.06.2017.
 */
/*
DROP TABLE IF EXISTS `gk_sys_site_exchange`;
CREATE TABLE `gk_sys_site_exchange` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(36) NOT NULL,
  `table_name` varchar(40) NOT NULL,
  `event` enum('CREATED','UPDATED','DELETED') NOT NULL,
  `data` text NOT NULL,
  `status` enum('NEW','IN_PROGRESS','PROCESSED') NOT NULL,
  `last_update` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `uuid_idx` (`uuid`),
  KEY `status_with_timestamp_idx` (`status`,`last_update`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/
@Entity
@Table(name = "gk_sys_site_exchange",
        indexes = {
                @Index(name = "uuid_idx", columnList = "uuid"),
                @Index(name = "status_with_timestamp_idx", columnList = "status,last_update")
        })
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid", length = 36)
    @NotNull
    private String uuid;

    @Column(name = "table_name", length = 64)
    @NotNull
    private String tableName;

    @Column(name = "event")
    @Convert(converter = EventTypeConverter.class)
    @NotNull
    private EventType event;

    @Column(name = "data")
    @NotNull
    @Type(type = "text")
    private String data;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    @NotNull
    private Status status;

    @Column(name = "last_update")
    @NotNull
    private Date lastUpdate;

    // TODO Add createdDateTime?

    public Integer getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType eventType) {
        this.event = eventType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Exchange exchange = (Exchange) o;

        if (id != null ? !id.equals(exchange.id) : exchange.id != null) return false;
        if (uuid != null ? !uuid.equals(exchange.uuid) : exchange.uuid != null) return false;
        if (tableName != null ? !tableName.equals(exchange.tableName) : exchange.tableName != null) return false;
        if (event != exchange.event) return false;
        if (data != null ? !data.equals(exchange.data) : exchange.data != null) return false;
        if (status != exchange.status) return false;
        return lastUpdate != null ? lastUpdate.equals(exchange.lastUpdate) : exchange.lastUpdate == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        result = 31 * result + (event != null ? event.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        return result;
    }


    public enum Status {
        NEW('N'), IN_PROGRESS('I'), PROCESSED('P');

        private final char value;

        Status(final char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }

        public static Status parse(final char value) {
            for (final Status status : Status.values()) {
                if (toLowerCase(status.toString().charAt(0)) == toLowerCase(value)) {
                    return status;
                }
            }
            return null;
        }
    }

    public enum EventType {
        CREATED('C'), UPDATED('U'), DELETED('D');

        private final char value;

        EventType(final char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }

        public static EventType parse(final char value) {
            for (final EventType status : EventType.values()) {
                if (toLowerCase(status.toString().charAt(0)) == toLowerCase(value)) {
                    return status;
                }
            }
            return null;
        }
    }

    public static class StatusConverter implements AttributeConverter<Status, Character> {

        @Override
        public Character convertToDatabaseColumn(final Status status) {
            return status.getValue();
        }

        @Override
        public Status convertToEntityAttribute(final Character value) {
            return Status.parse(value);
        }
    }

    public static class EventTypeConverter implements AttributeConverter<EventType, Character> {

        @Override
        public Character convertToDatabaseColumn(final EventType status) {
            return status.getValue();
        }

        @Override
        public EventType convertToEntityAttribute(final Character value) {
            return EventType.parse(value);
        }
    }
}
