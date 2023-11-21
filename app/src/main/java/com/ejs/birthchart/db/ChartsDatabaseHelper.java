package com.ejs.birthchart.db;

import static com.ejs.birthchart.utils.msg.log;
import static com.ejs.birthchart.utils.utils.showConfirmationDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ejs.birthchart.R;
import com.ejs.birthchart.data.ChartEntry;

import java.util.ArrayList;
import java.util.List;

public class ChartsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "charts.db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y nombres de las columnas
    public static final String TABLE_CHARTS = "charts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_CHART_TYPE = "chartType";
    public static final String COLUMN_FECHA1 = "fecha1";
    public static final String COLUMN_TIMEZONE1 = "timezone1";
    public static final String COLUMN_FECHA2 = "fecha2";
    public static final String COLUMN_TIMEZONE2 = "timezone2";
    public static final String COLUMN_LOCATION_TYPE = "locationType";
    public static final String COLUMN_CIUDADPAIS = "ciudadpais";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_HOUSE = "house";
    public static final String COLUMN_ZODIACAL = "zodiacal";

    public ChartsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla "charts"
        String createTableQuery = "CREATE TABLE " + TABLE_CHARTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1," +
                COLUMN_NOMBRE + " TEXT," +
                COLUMN_CHART_TYPE + " INTEGER," +
                COLUMN_FECHA1 + " DATETIME DEFAULT (strftime('%Y-%m-%d %H:%M', 'now', 'localtime'))," +
                COLUMN_TIMEZONE1 + " TEXT," +
                COLUMN_FECHA2 + " DATETIME DEFAULT (strftime('%Y-%m-%d %H:%M', 'now', 'localtime'))," +
                COLUMN_TIMEZONE2 + " TEXT," +
                COLUMN_LOCATION_TYPE + " INTEGER," +
                COLUMN_CIUDADPAIS + " TEXT," +
                COLUMN_LOCATION + " TEXT," +
                COLUMN_HOUSE + " TEXT," +
                COLUMN_ZODIACAL + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Manejar la actualización de la base de datos si existe una nueva versión
        // Esto puede implicar eliminar la tabla existente y crear una nueva
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_CHARTS;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public static boolean checkIfEntryExists(Context context, String nombre) {
        ChartsDatabaseHelper databaseHelper = new ChartsDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String selection = COLUMN_NOMBRE + " = ?";
        String[] selectionArgs = { nombre };

        Cursor cursor = db.query(TABLE_CHARTS, null, selection, selectionArgs, null, null, null);
        boolean entryExists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return entryExists;
    }
    public static void delData(Context context, DialogInterface.OnClickListener dialogOk) {
        ChartsDatabaseHelper databaseHelper = new ChartsDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        int rowsDeleted = db.delete(TABLE_CHARTS, null, null);

        db.close();

        if (rowsDeleted > 0) {
            showConfirmationDialog(context, dialogOk, null,context.getString(R.string.btn_ok),"",context.getString(R.string.str_delete));
            // Los datos se borraron correctamente
        } else {
            showConfirmationDialog(context, dialogOk, null,context.getString(R.string.btn_ok),"",context.getString(R.string.str_delete_fail));
            // No se encontraron registros para borrar o ocurrió un error durante el proceso
        }

    }
    public static List<ChartEntry> loadData(Context context) {
        ChartsDatabaseHelper databaseHelper = new ChartsDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] projection = { COLUMN_ID, COLUMN_NOMBRE, COLUMN_CHART_TYPE, COLUMN_FECHA1, COLUMN_TIMEZONE1,
                COLUMN_FECHA2, COLUMN_TIMEZONE2, COLUMN_LOCATION_TYPE, COLUMN_CIUDADPAIS,
                COLUMN_LOCATION, COLUMN_HOUSE, COLUMN_ZODIACAL
        };
        Cursor cursor = db.query(TABLE_CHARTS, projection, null, null, null, null, null);

        List<ChartEntry> chartEntries = new ArrayList<>();
        chartEntries.add(new ChartEntry(0, context.getString(R.string.str_select_data), 0,"","", "","",0,"","","",""));

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE));
            int chartType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHART_TYPE));
            String fecha1 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA1));
            String timezone1 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMEZONE1));
            String fecha2 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA2));
            String timezone2 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMEZONE2));
            int locationType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_TYPE));
            String ciudadPais = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CIUDADPAIS));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
            String house = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOUSE));
            String zodiacal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ZODIACAL));

            chartEntries.add(new ChartEntry(id, nombre, chartType, fecha1, timezone1, fecha2, timezone2, locationType, ciudadPais, location, house, zodiacal));
        }

        cursor.close();
        db.close();

        return chartEntries;
    }
    public static void saveData(Context context,DialogInterface.OnClickListener dialogOk, ContentValues values) {
        ChartsDatabaseHelper databaseHelper = new ChartsDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Consulta el valor máximo actual del campo id en la tabla
        String query = "SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_CHARTS;
        Cursor cursor = db.rawQuery(query, null);

        int nextId = 1; // Valor predeterminado si no hay registros en la tabla

        if (cursor.moveToFirst()) {
            int maxId = cursor.getInt(0);
            nextId = maxId + 1;
        }

        cursor.close();

        long newRowId = db.insert(TABLE_CHARTS, null, values);

        db.close();

        // Verificar si la inserción fue exitosa
        if (newRowId != -1) {
            showConfirmationDialog(context, dialogOk, null,context.getString(R.string.btn_ok),"",context.getString(R.string.str_save));
        } else {
            showConfirmationDialog(context, dialogOk, null,context.getString(R.string.btn_ok),"",context.getString(R.string.str_save_fail));
        }
    }
    public static void updateData(Context context,DialogInterface.OnClickListener dialogOk, ContentValues values, String name) {

        ChartsDatabaseHelper databaseHelper = new ChartsDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();


        String selection = COLUMN_NOMBRE + " = ?";
        String[] selectionArgs = { name };

        int rowsUpdated = db.update(TABLE_CHARTS, values, selection, selectionArgs);

        db.close();

        if (rowsUpdated > 0) {
            showConfirmationDialog(context, dialogOk, null, context.getString(R.string.btn_ok),"",context.getString(R.string.str_Updated));
        } else {
            showConfirmationDialog(context, dialogOk, null, context.getString(R.string.btn_ok),"",context.getString(R.string.str_Update_fail));

            // No se encontraron registros para actualizar o ocurrió un error durante la actualización
        }
    }
}