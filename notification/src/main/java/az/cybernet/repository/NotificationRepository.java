package az.cybernet.repository;

import az.cybernet.entity.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository {
    void save(NotificationEntity notification);
}
