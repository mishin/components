package org.talend.components.snowflake.runtime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Formatter {

    private static final ThreadLocal<SimpleDateFormat> dateFormatterLocal = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }

    };

    private SimpleDateFormat dateFormatter = dateFormatterLocal.get();

    private static final ThreadLocal<SimpleDateFormat> timeFormatterLocal = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss.SSS");
        }

    };

    private SimpleDateFormat timeFormatter = timeFormatterLocal.get();

    {
        // Time in milliseconds would mean time from midnight. It shouldn't be influenced by timezone differences.
        // That's why we have to use GMT.
        timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private static final ThreadLocal<SimpleDateFormat> timestampFormatterLocal = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX");
        }

    };

    private SimpleDateFormat timestampFormatter = timestampFormatterLocal.get();

    Object formatTimestampMillis(Object inputValue) {
        if (inputValue instanceof Date) {
            return getTimestampFormatter().format(inputValue);
        } else if (inputValue instanceof Long) {
            return getTimestampFormatter().format(new Date((Long) inputValue));
        } else {
            return inputValue;
        }
    }

    String formatDate(Object inputValue) {
        Date date = null;
        if (inputValue instanceof Date) {
            // Sometimes it can be sent as a Date object. We need to process it like a common date then.
            date = (Date) inputValue;
        } else if (inputValue instanceof Integer) {
            // If the date is int, it represents amount of days from 1970(no timezone). So if the date is
            // 14.01.2017 it shouldn't be influenced by timezones time differences. It should be the same date
            // in any timezone.
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            c.setTimeInMillis(0);
            c.add(Calendar.DATE, (Integer) inputValue);
            c.setTimeZone(TimeZone.getDefault());
            long timeInMillis = c.getTime().getTime();
            date = new Date(timeInMillis - c.getTimeZone().getOffset(timeInMillis));
        } else {
            // long is just a common timestamp value.
            date = new Date((Long) inputValue);
        }
        return getDateFormatter().format(date);
    }

    String formatTimeMillis(Object inputValue) {
        Date date = new Date((int) inputValue);
        return getTimeFormatter().format(date);
    }

    public SimpleDateFormat getDateFormatter() {
        return dateFormatter;
    }

    public SimpleDateFormat getTimeFormatter() {
        return timeFormatter;
    }

    public SimpleDateFormat getTimestampFormatter() {
        return timestampFormatter;
    }

}
