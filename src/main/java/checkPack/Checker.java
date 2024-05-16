package checkPack;

import checkPack.concreteRules.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
@Data
@Getter
@Setter
public class Checker {

    private final Stack<ErrorHandler> errors;
    private final List<Rule> rules;
    private final BadCSV badCSV;
    private final DescriptionRepository descriptionRepository;

    public Checker(){
        descriptionRepository = new DescriptionRepository();
        rules = new ArrayList<>();
        badCSV = new BadCSV();
        rules.add(new NotFound());
        rules.add(new NotForeignKey());
        rules.add(new BadQueryFormat());
        rules.add(new BadOrder());
        rules.add(new BadAlias());
        rules.add(new AgregationNotGroup());
        rules.add(new BadWhere());
        rules.add(new BadType());
        rules.add(new NotUsed());
        errors = new Stack<>();
    }

    public void check(String query){
        ErrorHandler err;
        for (Rule rule:rules){
            if((err = rule.check(query)) != null){
                errors.add(err);
                System.out.println(err);
            }
        }
    }

    public void checkCSV(String head,ArrayList<String> query){
        ErrorHandler err;
        if(((err = badCSV.check(head)) != null))
            errors.add(err);
    }

    public ArrayList<String> errorMessage(Stack<ErrorHandler> errorsMessage){
        ArrayList<String> msg = new ArrayList<>();
        MyError error;
        for(ErrorHandler eh : errorsMessage){
            if(descriptionRepository.getMap().containsKey(eh.getErrorCode())) {
                error = descriptionRepository.getMap().get(eh.getErrorCode());
                String desc = error.getError_description();
                String[] split = desc.split(" ");
                int j = 0;
                for(int i = 0 ; i<split.length ; i++)
                    if(split[i].equalsIgnoreCase("*")){
                        split[i] = eh.getErrorDescriptions().get(j);
                        j++;
                    }
                String finDesc = String.join(" ",split);
                msg.add(finDesc);
                String hint = error.getError_hint();
                String[] splith = hint.split(" ");
                int k = 0;
                for(int i = 0 ; i<splith.length ; i++)
                    if(splith[i].equalsIgnoreCase("*")){
                        splith[i] = eh.getErrorSugestions().get(k);
                        k++;
                    }
                String finHint = String.join(" ",splith);
                msg.add(finHint);
                }
            }
        return msg;
    }

}
