package weather.api.app.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import weather.api.app.Model.Data;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {  
} 
