package com.deange.wkrpt300.network.retrofit;

import com.deange.wkrpt300.Utils;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import retrofit.mime.TypedString;

public class PlainConverter implements Converter {

    @Override
    public String fromBody(final TypedInput body, final Type type) throws ConversionException {
        try {
            return new String(Utils.streamToByteArray(body.in()));
        } catch (IOException e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public TypedOutput toBody(final Object object) {
        return new TypedString(String.valueOf(object));
    }
}
