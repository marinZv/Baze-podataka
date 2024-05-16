package checkPack;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
@Data
@Getter
public class DescriptionRepository {

    private Map<String, MyError> map;
    MyError[] error;

    public DescriptionRepository(){
        map = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY,true);
            error = mapper.readValue(new File("src/BAZE.json"),MyError[].class);
            for(MyError e:error){
                map.put(e.getError_code(),e);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
