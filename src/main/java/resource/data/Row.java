package resource.data;


import java.util.HashMap;
import java.util.Map;

public class Row {

    private String name;
    private final Map<String, Object> fields;

    public Row(){
        this.fields = new HashMap<>();
    }

    public void addField(String fieldName, Object value){
        this.fields.put(fieldName, value);
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
    public void printRow(){

        for(Map.Entry<String, Object> entry : fields.entrySet()){
            System.out.println("Key: " + entry.getKey() + "Value: " + entry.getValue());
        }

    }
    */
}
