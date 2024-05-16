package checkPack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MyError {

    @JsonProperty("ERROR_CODE")
    private String error_code;
    @JsonProperty("ERROR_DESCRIPTION")
    private String error_description;
    @JsonProperty("ERROR_HINT")
    private String error_hint;

}
