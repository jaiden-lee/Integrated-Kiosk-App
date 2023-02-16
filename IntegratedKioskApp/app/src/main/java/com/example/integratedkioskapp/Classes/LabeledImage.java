package com.example.integratedkioskapp.Classes;

import java.io.File;

public class LabeledImage {
    public String imageFilePath;
    public String fileType; // 2 types: "Barcode", "Face"

    public LabeledImage (String path, String type) {
        this.imageFilePath = path;
        this.fileType = type;
    }
}
