package checkPack.concreteRules;

import checkPack.ErrorHandler;

public interface Rule {

    ErrorHandler check(String string);

}
