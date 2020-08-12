package net.lamgc.cgj.bot.util.parser;

import net.lamgc.utils.base.runner.StringParameterParser;

import java.text.DateFormat;
import java.util.Date;

public class DateParser implements StringParameterParser<Date> {

    private final DateFormat dateFormat;

    public DateParser(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Date parse(String strValue) throws Exception {
        return dateFormat.parse(strValue);
    }

    @Override
    public Date defaultValue() {
        return null;
    }
}
