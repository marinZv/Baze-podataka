package controler;

import controler.action.BulkImport;
import controler.action.Export;
import controler.action.Pretty;
import controler.action.Run;

public class ActionManager {

    private BulkImport bulkImport;
    private Export export;
    private Pretty pretty;
    private Run run;

    public ActionManager(){
        init();
    }

    private void init(){
        bulkImport = new BulkImport();
        export = new Export();
        pretty = new Pretty();
        run = new Run();
    }

    public BulkImport getBulkImport() {
        return bulkImport;
    }

    public Export getExport() {
        return export;
    }

    public Pretty getPretty() {
        return pretty;
    }

    public Run getRun() {
        return run;
    }
}
