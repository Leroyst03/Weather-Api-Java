package weather.api.app.Service;

import java.util.List;
import org.springframework.stereotype.Service;

import weather.api.app.DTO.DataDTO;
import weather.api.app.Model.Data;
import weather.api.app.Repository.DataRepository;

@Service
public class DataService {

    private final DataRepository dataRepository;

    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public Data saveFromDTO(DataDTO dto) {
        Data data = new Data();
        data.setDeviceId(dto.getDeviceId());
        data.setTemperature(dto.getTemperature());
        data.setHumidity(dto.getHumidity());
        return dataRepository.save(data);
    }

    public List<Data> getAllData() {
        return dataRepository.findAll();
    }
}
