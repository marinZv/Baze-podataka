package checkPack;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Data
@Getter
@Setter
public class ErrorHandler {

    private String errorCode;
    private List<String> errorDescriptions;
    private List<String> errorSugestions;

    public ErrorHandler(String errorCode){
        this.errorCode = errorCode;
        errorDescriptions = new ArrayList<>();
        errorSugestions = new ArrayList<>();
    }




}
