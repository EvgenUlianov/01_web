package ru.netology;

import java.nio.file.Path;

public class FileManager {

    public Path getFilePath(Request request){
        final String path = request.getPath();
        return Path.of(".", "public", path);
    }

    // SingleTone ++
    //multi tread safety

    private FileManager(){

    }

    private static class Holder {
        public static final FileManager FILE_MANAGER = new FileManager();
    }

    public static FileManager get()  {
        return FileManager.Holder.FILE_MANAGER;
    }

    // SingleTone --
}
