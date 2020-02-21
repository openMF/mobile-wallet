package org.mifos.mobilewallet.core.domain.model;

public class CheckBoxStatus {

    private String text;
    private boolean isChecked = false;

    public CheckBoxStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}
