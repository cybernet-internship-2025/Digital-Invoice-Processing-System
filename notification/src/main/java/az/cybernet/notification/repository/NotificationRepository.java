package az.cybernet.notification.repository;

import az.cybernet.notification.entity.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface NotificationRepository {
    void save(NotificationEntity notification);
}
