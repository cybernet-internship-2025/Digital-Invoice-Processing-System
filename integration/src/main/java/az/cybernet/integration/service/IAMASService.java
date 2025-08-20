package az.cybernet.integration.service;

import az.cybernet.integration.model.IAMASDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IAMASService {
    
    private static Map<String, IAMASDto> iamasDtoMap;

    @PostConstruct
    public void init() {
        iamasDtoMap = initData();
    }

    private Map<String, IAMASDto> initData() {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            ClassPathResource resource = new ClassPathResource("mock/iamas.yaml");
            return mapper.readValue(resource.getInputStream(),
                    mapper.getTypeFactory().constructMapType(HashMap.class, String.class, IAMASDto.class));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read IAMAS data from YAML file", e);
        }
    }


    public IAMASDto getPinData(String pin) {
        return Optional.ofNullable(iamasDtoMap.get(pin))
                .orElseThrow(()-> new RuntimeException("data not found"));
    }
}
