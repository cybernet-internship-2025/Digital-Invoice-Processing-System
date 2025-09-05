package az.cybernet.repository;

import az.cybernet.entity.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationRepository {
    void save(NotificationEntity notification);
}
