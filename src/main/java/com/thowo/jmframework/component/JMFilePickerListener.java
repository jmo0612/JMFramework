package com.thowo.jmframework.component;

import java.io.File;

public interface JMFilePickerListener {
    void onPicked(File chosen);
    void onCancel();
}
