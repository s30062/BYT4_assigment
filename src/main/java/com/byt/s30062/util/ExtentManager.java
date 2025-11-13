package com.byt.s30062.util;

import java.io.*;
import java.util.List;

public class ExtentManager {
    public static <T> void saveExtent(List<T> extent, String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(extent);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadExtent(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<T>) in.readObject();
        }
    }
}
