package oof;

import oof.model.module.Module;
import oof.model.module.Semester;

public class SelectedInstance {

    private static SelectedInstance selectedInstance;
    private Semester semester;
    private Module module;

    private SelectedInstance() {
    }

    /**
     * Returns an instance of the SelectedInstance class.
     *
     * @return an instance of the SelectedInstance class.
     */
    public static SelectedInstance getInstance() {
        if (selectedInstance == null) {
            selectedInstance = new SelectedInstance();
        }
        return selectedInstance;
    }

    public void selectSemester(Semester semester) {
        this.semester = semester;
        this.module = null;
    }

    public Semester getSemester() {
        return this.semester;
    }

    public void selectModule(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return this.module;
    }
}