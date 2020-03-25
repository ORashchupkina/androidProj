package ru.infoenergo.android.common;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSqlite {
    static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    static SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);

    public DateSqlite(Date date){
        this.date = date;
    }
    public Date date = null;

    public String toString() {
        return dateToSqlite(date);
    }

    @TypeConverter
    static public String dateToSqlite(Date dt) {
        return sdf.format(dt);
    }

    @TypeConverter
    static public Date stringToDate(String dateInString) {
        Date date = null;
        try {
            date = sdf.parse(dateInString);
        } catch (ParseException ex) {
//            Logger.getLogger(DateSqlite.class.getName()).log(Level.WARNING, "{01AE5D95-ABA2-4216-83E0-A926C3F87B54}", ex);
            // если не удалось пропарсить из-за того, что нет миллисекунд, то пробуем, добавив к строке миллисекунды
            // если опять ошибка, то дело не в миллисекундах
            dateInString = dateInString.concat(".001");
            try {
                date = sdf.parse(dateInString);
            } catch (ParseException ex1) {
//                Logger.getLogger(DateSqlite.class.getName()).log(Level.WARNING, "{37F77195-4C07-42FA-8185-4E3A41A8510B}", ex1);
            }
            return date;
        }
        return date;
    }
}
