package com.ejs.birthchart.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class FileUtils {

    /**
     *
     * @param context
     * @return
     */
    public static File getDataDir(Context context) {

        String path = context.getFilesDir().getAbsolutePath() + "/zip";

        File file = new File(path);

        if(!file.exists()) {

            file.mkdirs();
        }

        return file;
    }

    /**
     *
     * @param context
     * @param folder
     * @return
     */
    public static File getDataDir(Context context, String folder) {

        String path = context.getFilesDir().getAbsolutePath() + "/" + folder;

        File file = new File(path);

        if(!file.exists()) {

            file.mkdirs();
        }

        return file;
    }

    /**
     *
     * @param context
     * @param folder
     * @return
     */
    public static File getExternalAppDir(Context context, String folder) {

        String path = context.getExternalFilesDir(null).getAbsolutePath() + "/" + folder;

        File file = new File(path);

        if(!file.exists()) {

            file.mkdirs();
        }

        return file;
    }
    public static File getExternalAppDir1(Context context) {

        String path = context.getExternalFilesDir(null).getAbsolutePath() + "/" ;

        File file = new File(path);

        if(!file.exists()) {

            file.mkdirs();
        }

        return file;
    }

    /**
     *
     * @param fileOrDirectory
     * @return
     */
    public static boolean DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
            {
                child.delete();
                DeleteRecursive(child);
            }

        return fileOrDirectory.delete();
    }

    public static boolean isDir(File file){
        if (file.isDirectory()) {
            return true;
        }
        return false;
    }
    public static boolean emptyDir(File dir){
        String[] files = dir.list();
        if (files.length == 0) {
            return true;
        }
        return false;
    }
    public static boolean checkDataexist(Context context, String Path, String theme, boolean demo){
        boolean existAll = false;
        SharedPreferences sharedPref = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        //String theme = sharedPref.getString("imageAssetsFolder", "china/");
        //String Path = FileUtils.getExternalAppDir(context, "themes/").getAbsolutePath();
        String Pathnight, Pathday, Pathdawn, Pathtwilight;
        if (demo){
            Pathnight = Path + "/" + theme + "_demo/night/";
            Pathday = Path + "/" + theme + "_demo/day/";
            Pathdawn = Path + "/" + theme + "_demo/dawn/";
            Pathtwilight = Path + "/" + theme + "_demo/twilight/";
        } else {
            Pathnight = Path + "/" + theme + "_full/night/";
            Pathday = Path + "/" + theme + "_full/day/";
            Pathdawn = Path + "/" + theme + "_full/dawn/";
            Pathtwilight = Path + "/" + theme + "_full/twilight/";
        }
        File dirnight = new File(Pathnight);
        File dirday = new File(Pathday);
        File dirdawn = new File(Pathdawn);
        File dirtwilight = new File(Pathtwilight);
        if (dirnight.exists() && dirday.exists() && dirdawn.exists() && dirtwilight.exists() &&
                dirnight.listFiles().length >= 0 && dirday.listFiles().length >= 0 && dirdawn.listFiles().length >= 0
                && dirtwilight.listFiles().length >= 0){
            existAll = true;
        }
        return existAll;

    }
}
