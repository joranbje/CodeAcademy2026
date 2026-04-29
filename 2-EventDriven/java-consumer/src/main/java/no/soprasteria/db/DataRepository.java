package no.soprasteria.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<MessageData, Long> {

    @Query("FROM MessageData ORDER BY createdAt desc limit 50")
    List<MessageData> findLatest();
}
